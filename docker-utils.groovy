def formatContainerName() {
	// TODO
}

def findContainerIdByName(String containerName) {
	try {
		// get full container ID
		containerId = sh (
	        script: "docker ps --no-trunc -qaf name=${containerName}",
	        returnStdout: true
	    ).trim()
	    echo "Container found for name ${containerName} : ${containerId}"
	    return containerId
	} catch (error) {
		echo 'Error while looking for container with name ${containerName}: ${error}'
	}
}

def checkContainerExistByName(String containerName) {
	return ( 
		sh (
			script: "docker ps -qaf name=${containerName}",
			returnStdout: true
		)) ? true : false
}

def checkContainerExistById(String containerId) {
	return ( 
		sh (
			script: "docker ps -qaf id=${containerId}",
			returnStdout: true
		)) ? true : false
}


def stopAndRemoveContainer(String containerId) {
	try {
		// remove container
		sh "docker rm -f ${containerId}"
		echo "removed container with ID: ${containerId}"
	} catch (error) {
		echo 'Error while removing container ${containerName} : ${error}'
	}
}

def pullAndRunImage(String dockerRegistry, String maintainer, String imageName, String imageHost, String buildVersion, String dockerArgs) {
  echo "Run image(${imageName}) with buildVersion(${buildVersion}) with the arguments (${dockerArgs})";
  sh "docker pull ${dockerRegistry}/${maintainer}/${imageName}:${buildVersion}";
  docker.image("${dockerRegistry}/${maintainer}/${imageName}:${buildVersion}").run("--restart=unless-stopped --hostname ${imageHost} --name ${imageName} ${dockerArgs}");
}

return this
