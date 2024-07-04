<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Test</title>
    <script type="text/javascript">
        var ws;
        function connect() {
            ws = new WebSocket("ws://localhost:8080/websocket");
            ws.onmessage = function(event) {
                var messages = document.getElementById('messages');
                messages.innerHTML += '<br>' + event.data;
            };
        }

        function sendMessage() {
            var message = document.getElementById('message').value;
            ws.send(message);
        }
    </script>
</head>
<body onload="connect()">
    <input type="text" id="message">
    <button onclick="sendMessage()">Send</button>
    <div id="messages"></div>
</body>
</html>
