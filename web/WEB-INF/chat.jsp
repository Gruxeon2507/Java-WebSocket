<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>Chat</title>
</head>
<body>
    <div id="messages"></div>
    <form onsubmit="sendMessage(); return false;">
        <input type="text" id="messageInput">
        <button type="submit">Send</button>
    </form>
    
    <script>
        const socket = new WebSocket('ws://localhost:8080/chat');

        socket.addEventListener('message', (event) => {
            const message = event.data;
            const messageElement = document.createElement('div');
            messageElement.textContent = message;
            document.querySelector('#messages').appendChild(messageElement);
        });

        function sendMessage() {
            const messageInput = document.querySelector('#messageInput');
            const message = messageInput.value;
            socket.send(message);
            messageInput.value = '';
        }
    </script>
</body>
</html>
