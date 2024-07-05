package com.omega.websocket;

import com.omega.redis.RedisConfig;
import com.omega.redis.RedisPublisher;
import com.omega.redis.RedisSubscriber;
import redis.clients.jedis.Jedis;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket/{topic}")
public class MyWebSocketEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSocketEndpoint.class);
    private static final Map<String, Set<MyWebSocketEndpoint>> topicSubscribers = new ConcurrentHashMap<>();
    private static RedisSubscriber redisSubscriber = new RedisSubscriber();
    private static RedisPublisher redisPublisher = new RedisPublisher();
    private Session session;
    private String topic;

    @OnOpen
    public void onOpen(@PathParam("topic") String topic, Session session) {
        this.topic = topic;
        this.session = session;

        // Subscribe to Redis topic
        topicSubscribers.computeIfAbsent(topic, k -> new CopyOnWriteArraySet<>()).add(this);
        redisSubscriber.subscribe(topic);

        LOGGER.info("Connected: session {} to topic: {}", session.getId(), topic);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Publish message to Redis
        redisPublisher.publish(topic, message);

        LOGGER.info("Received message: {} from session: {} on topic: {}", message, session.getId(), topic);
    }

    @OnClose
    public void onClose(Session session) {
        topicSubscribers.getOrDefault(topic, new CopyOnWriteArraySet<>()).remove(this);

        LOGGER.info("Disconnected: session {} from topic: {}", session.getId(), topic);
    }

    public static void broadcastMessage(String topic, String message) {
        Set<MyWebSocketEndpoint> subscribers = topicSubscribers.getOrDefault(topic, new CopyOnWriteArraySet<>());

        for (MyWebSocketEndpoint endpoint : subscribers) {
            try {
                endpoint.session.getBasicRemote().sendText(message);
                LOGGER.info("Sent message: {} to session: {} on topic: {}", message, endpoint.session.getId(), topic);
            } catch (Exception e) {
                LOGGER.error("Failed to send message to session: {}", endpoint.session.getId(), e);
            }
        }
    }
}
