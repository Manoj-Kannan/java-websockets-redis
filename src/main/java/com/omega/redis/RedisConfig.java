package com.omega.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfig {

    private static final JedisPool jedisPoolPublisher;
    private static final JedisPool jedisPoolSubscriber;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10); // Maximum number of connections in the pool
        poolConfig.setMaxIdle(5);   // Maximum number of idle connections in the pool
        poolConfig.setMinIdle(1);   // Minimum number of idle connections in the pool

        jedisPoolPublisher = new JedisPool(poolConfig, "localhost", 6379);
        jedisPoolSubscriber = new JedisPool(poolConfig, "localhost", 6379);
    }

    public static JedisPool getPublisherPool() {
        return jedisPoolPublisher;
    }

    public static JedisPool getSubscriberPool() {
        return jedisPoolSubscriber;
    }

    /*
    Publishing:     RedisPublisher publishes messages to Redis channels using a dedicated connection from the publisher pool.
    Subscribing:    RedisSubscriber listens for messages from Redis channels using a dedicated connection from the subscriber pool and broadcasts them to WebSocket clients.

    Why isolate Producers & Consumers?
    Decoupling Logic:
        Scenario:   An error occurs during message publishing due to a network issue or Redis connectivity problem.
        Benefit:    Errors in publishing won't affect message reception and broadcasting (subscribing), and vice versa.
                    Each component can handle errors independently, ensuring that failures in one part of the system do not cascade into other parts.

    Concurrency Handling:
        Scenario:   Several WebSocket clients are actively publishing and subscribing to messages within the application.
        Benefit:    Parallel operations are handled efficiently:
                    Multiple instances of WebSocketServer can handle concurrent WebSocket connections without blocking or waiting for Redis operations.
                    RedisPublisher and RedisSubscriber manage their respective operations asynchronously, allowing for smooth and responsive communication between WebSocket clients and Redis.
                    This separation ensures that message publishing (write operations) and message subscribing (read operations) do not compete for the same resources within Redis, reducing latency and improving overall system performance.
     */
}
