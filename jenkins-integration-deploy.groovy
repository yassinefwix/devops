node {

    
    // import scripts
    def rootDir = pwd()
    def dockerUtils = load("${rootDir}@script/docker-utils.groovy")
    
    final MAINTAINER = 'geekfive'
    // Docker Registry
    final DOCKER_REGISTRY = 'registry.hub.docker.com'
    final DOCKER_REGISTRY_URL = 'https://'+ DOCKER_REGISTRY
    final DOCKER_REGISTRY_CREDENTIALS = 'dockerHub'

    final WEB_FRONT_NAME = "front"
    final WEB_BACK_NAME = "f2ml-integration-web-back"

    // networks
    final NETWORK_FRONT = "geekfive/front"
    final NETWORK_BACK = "f2ml-integration-backend"
    final WEB_FRONT_PORT = '82'
    final WEB_BACK_PORT = '81'

    // volumes
    //final IMAGES_VOLUME = '/home/f2ml/web/static'

    stage("WEB front & back containers deploy") {
        //sh "docker network create devops-net"
        // find containers IDs
        def containerWebFrontId = dockerUtils.findContainerIdByName('/front')
        def containerWebBackId = dockerUtils.findContainerIdByName(WEB_BACK_NAME)

        // remove previous containers if needed
        if (containerWebFrontId != null) {
            dockerUtils.stopAndRemoveContainer(containerWebFrontId)
            dockerUtils.stopAndRemoveContainer("integ-mongo")
        }
        if (containerWebBackId != null) {
            //dockerUtils.stopAndRemoveContainer(containerWebBackId)
        }
        // start new containers (web back & front)
        // dockerUtils.pullAndRunImage(DOCKER_REGISTRY, MAINTAINER, WEB_BACK_NAME, WEB_BACK_NAME, 'latest', "--network ${NETWORK_FRONT} --network ${NETWORK_BACK} -p ${WEB_BACK_PORT}:80 -e PLATFORM=${PLATEFORM} -v ${IMAGES_VOLUME}:/var/www/f2m_lease/web/static  -v /home/f2ml/ci-phpunit:/var/www/f2m_lease/web/ci") // -v ./docker/logs/nginx_back/:/var/log/nginx -v ./docker/logs/php-fpm_back/:/var/log/php-fpm
        // dockerUtils.pullAndRunImage(DOCKER_REGISTRY, MAINTAINER, WEB_FRONT_NAME, WEB_FRONT_NAME, 'latest', "--network ${NETWORK_FRONT} --network ${NETWORK_BACK} -p ${WEB_FRONT_PORT}:80 -e PLATFORM=${PLATEFORM} -v ${IMAGES_VOLUME}:/var/www/f2m_lease/dist/web/static") // -v ./docker/logs/apache2_front/:/var/log/apache2
  
        // start new containers (web back & front)
        // dockerUtils.pullAndRunImage(DOCKER_REGISTRY, MAINTAINER, WEB_BACK_NAME, WEB_BACK_NAME, 'latest', "--network ${NETWORK_FRONT} --network ${NETWORK_BACK} -p ${WEB_BACK_PORT}:80" )
        sh "docker pull mongo";
        
        docker.image("mongo").run("--restart=unless-stopped --hostname mongo --network devops-net --name integ-mongo -d -p 27017:27017 -v ~/data:/data/db ");
        
        dockerUtils.pullAndRunImage(DOCKER_REGISTRY, MAINTAINER, WEB_FRONT_NAME, WEB_FRONT_NAME, 'latest','-p 8002:8002 -p 8000:8000 --network devops-net')        
    }
   

    stage("Docker system cleanup") {
        // will remove all stoped container, unused images and tangling volumes & netwoks
        sh "docker system prune -f"
        // display running containers
        sh "docker ps -a"
    }
}
