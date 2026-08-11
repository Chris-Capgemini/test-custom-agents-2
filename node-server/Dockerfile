FROM node:18-alpine

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY src/ ./src/

EXPOSE 1337

CMD ["node", "src/WebsocketServer.js"]
