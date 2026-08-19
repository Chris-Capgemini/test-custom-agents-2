const { WebSocketServer } = require('ws');
const http = require('http');

// Global variables
const webSocketsServerPort = 1337;
const messages = [];
const clients = [];

// HTTP server
const server = http.createServer();
server.listen(webSocketsServerPort, function () {
console.log((new Date()) + ' Server is listening on port ' + webSocketsServerPort);
});

// WebSocket server
const wsServer = new WebSocketServer({ server });

// This callback function is called every time someone
// tries to connect to the WebSocket server
wsServer.on('connection', function (ws, request) {
const origin = request.headers['origin'] || 'unknown';
console.log((new Date()) + ' Connection from origin ' + origin + '.');

clients.push(ws);
console.log((new Date()) + ' Connection accepted.');

// user sent some message
ws.on('message', function (data) {
const json = data.toString();

// log and broadcast the message
console.log((new Date()) + ' Received Message ' + json);

// broadcast message to all connected clients
for (let i = 0; i < clients.length; i++) {
if (clients[i].readyState === ws.OPEN) {
clients[i].send(json);
}
}
});

// user disconnected
ws.on('close', function () {
const remoteAddress = request.socket.remoteAddress;
console.log((new Date()) + ' Peer ' + remoteAddress + ' disconnected.');
// remove user from the list of connected clients
const index = clients.indexOf(ws);
if (index !== -1) {
clients.splice(index, 1);
}
});
});
