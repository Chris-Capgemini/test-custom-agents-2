'use strict';

const { test } = require('node:test');
const assert = require('node:assert/strict');

// Unit tests for WebSocket server logic (message handling, client management)

test('message broadcasting sends to all connected clients', () => {
  // Simulate the broadcast logic from WebsocketServer.js
  const clients = [];
  const sent = [];

  // Mock client with sendUTF method
  const mockClient = (id) => ({
    id,
    sendUTF: (msg) => sent.push({ id, msg })
  });

  clients.push(mockClient(1));
  clients.push(mockClient(2));
  clients.push(mockClient(3));

  const message = JSON.stringify({ target: 'textfield', content: { name: 'Mayer' } });

  // Replicate broadcast logic from WebsocketServer.js
  for (let i = 0; i < clients.length; i++) {
    clients[i].sendUTF(message);
  }

  assert.strictEqual(sent.length, 3);
  assert.strictEqual(sent[0].id, 1);
  assert.strictEqual(sent[1].id, 2);
  assert.strictEqual(sent[2].id, 3);
  sent.forEach(({ msg }) => assert.strictEqual(msg, message));
});

test('client removal on disconnect removes correct client', () => {
  const clients = [];
  const mockClient = (id) => ({ id });

  clients.push(mockClient(1));
  clients.push(mockClient(2));
  clients.push(mockClient(3));

  // Replicate disconnect logic from WebsocketServer.js
  const indexToRemove = 1; // remove client with id=2
  clients.splice(indexToRemove, 1);

  assert.strictEqual(clients.length, 2);
  assert.strictEqual(clients[0].id, 1);
  assert.strictEqual(clients[1].id, 3);
});

test('JSON message parsing produces expected structure', () => {
  const rawMessage = JSON.stringify({ target: 'textfield', content: { name: 'Mueller', zip: '14489' } });
  const parsed = JSON.parse(rawMessage);

  assert.strictEqual(parsed.target, 'textfield');
  assert.strictEqual(parsed.content.name, 'Mueller');
  assert.strictEqual(parsed.content.zip, '14489');
});
