const webSocketServer = require('websocket').server;
const http = require('http');

// Global variables
const webSocketsServerPort = 1337;
let clients = [];

// HTTP server
const server = http.createServer(function (request, response) {
});
server.listen(webSocketsServerPort, function () {
	console.log((new Date()) + " Server is listening on port "
		+ webSocketsServerPort);
});

server.on('error', function (err) {
	console.error((new Date()) + ' HTTP server error: ' + err.message);
});

// WebSocket server
const wsServer = new webSocketServer({
	// WebSocket server is tied to a HTTP server. WebSocket
	// request is just an enhanced HTTP request:
	// http://tools.ietf.org/html/rfc6455#page-6
	httpServer: server,
	maxReceivedMessageSize: 0x10000
});

// This callback function is called every time someone
// tries to connect to the WebSocket server
wsServer.on('request', function (request) {
	console.log((new Date()) + ' Connection from origin ' + request.origin
		+ '.');

	// accept connections from any origin
	const connection = request.accept(null, request.origin);

	clients.push(connection);

	console.log((new Date()) + ' Connection accepted.');

	// user sent some message
	connection.on('message', function (message) {
		if (message.type === 'utf8') {
			const json = message.utf8Data;

			// log and broadcast the message to all other connected clients
			console.log((new Date()) + ' Received Message ' + json);
			for (const client of clients) {
				if (client !== connection && client.connected) {
					client.sendUTF(json);
				}
			}
		}
	});

	connection.on('error', function (err) {
		console.error((new Date()) + ' Connection error: ' + err.message);
	});

	// user disconnected
	connection.on('close', function (reasonCode, description) {
		console.log((new Date()) + " Peer " + connection.remoteAddress
			+ " disconnected.");
		// remove user from the list of connected clients
		clients = clients.filter(c => c !== connection);
	});
});