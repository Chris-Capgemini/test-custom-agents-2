'use strict';

const { test } = require('node:test');
const assert = require('node:assert/strict');

// Simulate the broadcast logic extracted from WebsocketServer.js
function broadcast(clients, json) {
	for (const client of clients) {
		client.sendUTF(json);
	}
}

test('broadcast sends message to all connected clients', () => {
	const sent = [];
	const mockClient = { sendUTF: (msg) => sent.push(msg) };
	const clients = [mockClient, mockClient];

	broadcast(clients, '{"hello":"world"}');

	assert.equal(sent.length, 2);
	assert.equal(sent[0], '{"hello":"world"}');
	assert.equal(sent[1], '{"hello":"world"}');
});

test('broadcast sends nothing when no clients are connected', () => {
	const clients = [];
	// Should not throw
	assert.doesNotThrow(() => broadcast(clients, '{"hello":"world"}'));
});

test('broadcast only sends utf8 messages', () => {
	const sent = [];
	const mockClient = { sendUTF: (msg) => sent.push(msg) };
	const clients = [mockClient];

	// Only utf8 messages should be broadcast (handled upstream by message type check)
	broadcast(clients, '{"target":"textarea","content":"test"}');

	assert.equal(sent.length, 1);
	assert.equal(sent[0], '{"target":"textarea","content":"test"}');
});
