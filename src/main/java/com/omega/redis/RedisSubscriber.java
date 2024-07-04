package com.omega.redis;

import com.omega.websocket.MyWebSocketEndpoint;
import redis.clients.jedis.JedisPubSub;

public class RedisSubscriber extends JedisPubSub {
    private MyWebSocketEndpoint webSocketEndpoint;

    public RedisSubscriber(MyWebSocketEndpoint webSocketEndpoint) {
        this.webSocketEndpoint = webSocketEndpoint;
    }

    @Override
    public void onMessage(String channel, String message) {
        webSocketEndpoint.broadcastMessage(message);
    }
}
