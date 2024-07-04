package com.omega.redis;

import redis.clients.jedis.Jedis;

public class RedisPublisher {
    public void publish(String channel, String message) {
        try (Jedis jedis = RedisConfig.getJedis()) {
            jedis.publish(channel, message);
        }
    }
}
