// WebSocket connection URL
const wsUrl = 'ws://192.168.1.69:6969';

const messagesDiv = document.getElementById('messages');
const messageForm = document.getElementById('message-form');
const messageInput = document.getElementById('message-input');

// WebSocket connection
const socket = new WebSocket(wsUrl);

// open
socket.addEventListener('open', function(event) {
    console.log('WebSocket connection opened.');
});

// messages
socket.addEventListener('message', function(event) {
    const message = event.data;
    console.log('Message received from server:', message);
    const messageElement = document.createElement('div');
    messageElement.textContent = message;
    messagesDiv.appendChild(messageElement);
});

// close
socket.addEventListener('close', function(event) {
    console.log('WebSocket connection closed.');
});

// submit
messageForm.addEventListener('submit', function(event) {
    event.preventDefault();
    const message = messageInput.value;
    socket.send(message);
    messageInput.value = '';
});
