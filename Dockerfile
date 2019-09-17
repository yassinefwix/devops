FROM node:7-onbuild

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .

# tell docker what port to expose
EXPOSE 8000

CMD ["npm", "start"]
