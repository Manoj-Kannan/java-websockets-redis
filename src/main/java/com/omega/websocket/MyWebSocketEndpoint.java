package com.omega.websocket;

import com.omega.redis.RedisConfig;
import com.omega.redis.RedisPublisher;
import com.omega.redis.RedisSubscriber;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket")
public class MyWebSocketEndpoint {

    private static final CopyOnWriteArraySet<MyWebSocketEndpoint> webSocketSet = new CopyOnWriteArraySet<>();
    private static final RedisPublisher redisPublisher = new RedisPublisher();
    private Session session;

    public MyWebSocketEndpoint() {
        new Thread(() -> {
            RedisSubscriber subscriber = new RedisSubscriber(this);
            try (Jedis jedis = RedisConfig.getJedis()) {
                jedis.subscribe(subscriber, "websocket-channel");
            }
        }).start();
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        System.out.println("Connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        redisPublisher.publish("websocket-channel", message);
    }

    @OnClose
    public void onClose(Session session) {
        webSocketSet.remove(this);
        System.out.println("Disconnected: " + session.getId());
    }

    public void broadcastMessage(String message) {
        for (MyWebSocketEndpoint endpoint : webSocketSet) {
            try {
                endpoint.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
