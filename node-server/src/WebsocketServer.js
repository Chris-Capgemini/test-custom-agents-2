const WebSocketServer = require('websocket').server;
const http = require('http');

// Global variables
const webSocketsServerPort = 1337;
const clients = [];

// HTTP server
const server = http.createServer((_request, _response) => {});
server.listen(webSocketsServerPort, () => {
	console.log(`${new Date()} Server is listening on port ${webSocketsServerPort}`);
});

// WebSocket server
const wsServer = new WebSocketServer({
	// WebSocket server is tied to a HTTP server. WebSocket
	// request is just an enhanced HTTP request:
	// http://tools.ietf.org/html/rfc6455#page-6
	httpServer: server
});

// This callback function is called every time someone
// tries to connect to the WebSocket server
wsServer.on('request', (request) => {
	console.log(`${new Date()} Connection from origin ${request.origin}.`);

	// later we maybe allow cross-origin requests
	const connection = request.accept(null, request.origin);

	// we need to know client index to remove them on 'close' event
	const index = clients.push(connection) - 1;

	console.log(`${new Date()} Connection accepted.`);

	// user sent some message
	connection.on('message', (message) => {
		if (message.type === 'utf8') {
			const json = message.utf8Data;

			// log and broadcast the message
			console.log(`${new Date()} Received Message ${json}`);

			// broadcast message to all connected clients
			for (const client of clients) {
				client.sendUTF(json);
			}
		}
	});

	// user disconnected
	connection.on('close', (closedConnection) => {
		console.log(`${new Date()} Peer ${closedConnection.remoteAddress} disconnected.`);
		// remove user from the list of connected clients
		clients.splice(index, 1);
	});
});