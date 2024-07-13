package com.omega.websocket;

import com.omega.config.TopicsConfig;
import com.omega.redis.RedisPublisher;
import com.omega.redis.RedisSubscriber;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket/{topic}")
public class MyWebSocketEndpoint {
    // TODO - Message Producer gets the message (so message is duplicated)
    // TODO - Why should we subecribe to a topic to send messages?

    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSocketEndpoint.class);
    private static final Map<String, Set<MyWebSocketEndpoint>> topicSubscribers = new ConcurrentHashMap<>();
    private static RedisSubscriber redisSubscriber = new RedisSubscriber();
    private static RedisPublisher redisPublisher = new RedisPublisher();
    private static final TopicsConfig topicsConfig = new TopicsConfig();
    private Session session;
    private String topic;

    static {
        // Initialize topicsConfig
        topicsConfig.loadTopics();
    }

    @OnOpen
    public void onOpen(@PathParam("topic") String topic, Session session) {
        List<String> validTopics = topicsConfig.getTopics();

        if (validTopics.contains(topic)) {
            // Proceed with handling the session
            LOGGER.info("Session opened for topic: {}", topic);

            this.topic = topic;
            this.session = session;

            // Subscribe to Redis topic
            topicSubscribers.computeIfAbsent(topic, k -> new CopyOnWriteArraySet<>()).add(this);
            redisSubscriber.subscribe(topic);

            LOGGER.info("Connected: session {} to topic: {}", session.getId(), topic);
        } else {
            // Close the session if the topic is invalid
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid topic"));
                LOGGER.warn("Attempted to open session with invalid topic: {}", topic);
            } catch (Exception e) {
                LOGGER.error("Error closing session with invalid topic: {}", topic, e);
            }
        }
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
