<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>WebSocket Client</title>
</head>
<body>
    <h2>WebSocket Client</h2>
    <div>
        <label for="topics">Select a topic:</label>
        <select id="topics">
            <option value="topic1">Topic 1</option>
            <option value="topic2">Topic 2</option>
            <option value="topic3">Topic 3</option>
        </select>
        <button onclick="subscribe()">Subscribe</button>
        <button onclick="unsubscribe()">Unsubscribe</button>
    </div>
    <div>
        <input type="text" id="message" placeholder="Enter message">
        <button onclick="sendMessage()">Send</button>
    </div>
    <div id="output"></div>

    <script>
        let ws;
        let currentTopic = '';

        function subscribe() {
            const topic = document.getElementById("topics").value;
            if (ws) {
                ws.close();
            }
            ws = new WebSocket("ws://localhost:8080/websocket/" + topic);
            currentTopic = topic;
            ws.onopen = () => document.getElementById("output").innerHTML = "Subscribed to " + topic;
            ws.onmessage = (event) => document.getElementById("output").innerHTML += "<br>" + event.data;
            ws.onclose = () => document.getElementById("output").innerHTML += "<br>Unsubscribed from " + topic;
        }

        function unsubscribe() {
            if (ws) {
                ws.close();
                ws = null;
                currentTopic = '';
                document.getElementById("output").innerHTML = "You are not subscribed to any topic.";
            }
        }

        function sendMessage() {
            const message = document.getElementById("message").value;
            if (ws && currentTopic) {
                ws.send(message);
            } else {
                document.getElementById("output").innerHTML = "You are not subscribed to any topic.";
            }
        }
    </script>
</body>
</html>
