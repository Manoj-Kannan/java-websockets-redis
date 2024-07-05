package com.omega.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class RedisPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisPublisher.class);

    public void publish(String topic, String message) {
        try (Jedis jedis = RedisConfig.getPublisherPool().getResource()) {
            jedis.publish(topic, message);
            LOGGER.info("Message published to topic {}: {}", topic, message);
        } catch (Exception e) {
            LOGGER.error("Error publishing message to topic {}", topic, e);
        }
    }
}
