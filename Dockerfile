FROM node:10

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .

# tell docker what port to expose
EXPOSE 8000

CMD ["npm", "start"]
