<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Topic Subscription</title>
    <script type="text/javascript">
        var ws;
        var subscribedTopic = null;

        function listTopics() {
            // Mocked list of topics for demonstration purposes
            var topics = ["topic1", "topic2", "topic3"];
            var topicList = document.getElementById('topic-list');
            topicList.innerHTML = '';

            topics.forEach(function(topic) {
                var topicItem = document.createElement('div');
                topicItem.innerHTML = topic;
                topicItem.style.cursor = 'pointer';
                topicItem.onclick = function() {
                    subscribeToTopic(topic);
                };
                topicList.appendChild(topicItem);
            });
        }

        function subscribeToTopic(topic) {
            if (ws) {
                ws.close(); // Close the current connection if any
            }
            subscribedTopic = topic;
            ws = new WebSocket("ws://localhost:8080/websocket/" + topic);
            ws.onmessage = function(event) {
                var messages = document.getElementById('messages');
                messages.innerHTML += '<br>' + event.data;
            };
            ws.onopen = function() {
                alert('Subscribed to ' + topic);
            };
            ws.onclose = function() {
                alert('Unsubscribed from ' + topic);
            };
        }

        function sendMessage() {
            if (!ws || ws.readyState !== WebSocket.OPEN) {
                alert('You are not subscribed to any topic.');
                return;
            }
            var message = document.getElementById('message').value;
            ws.send(message);
        }

        function unsubscribe() {
            if (ws) {
                ws.close();
                subscribedTopic = null;
                document.getElementById('messages').innerHTML = '';
            }
        }

        window.onload = function() {
            listTopics();
        };
    </script>
</head>
<body>
    <div>
        <h3>Available Topics</h3>
        <div id="topic-list"></div>
    </div>
    <div>
        <input type="text" id="message" placeholder="Enter message">
        <button onclick="sendMessage()">Send</button>
        <button onclick="unsubscribe()">Unsubscribe</button>
    </div>
    <div id="messages"></div>
</body>
</html>
