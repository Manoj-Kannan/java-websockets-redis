package com.omega.redis;

import com.omega.websocket.MyWebSocketEndpoint;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Jedis;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class RedisSubscriber extends JedisPubSub {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubscriber.class);

    @Override
    public void onMessage(String channel, String message) {
        LOGGER.info("Message received from topic {}: {}", channel, message);
        MyWebSocketEndpoint.broadcastMessage(channel, message);
    }

    public void subscribe(String topic) {
        new Thread(() -> {
            try (Jedis jedis = RedisConfig.getSubscriberPool().getResource()) {
                jedis.subscribe(this, topic);
            } catch (Exception e) {
                LOGGER.error("Failed to subscribe to topic {}", topic, e);
            }
        }).start();
    }
}
