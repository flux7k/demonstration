package io.github.flux7k.demonstration.adapter.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@ImportAutoConfiguration(exclude = RedisRepositoriesAutoConfiguration.class)
@Configuration
public class RedisConfiguration {

    private final ReactiveRedisConnectionFactory connectionFactory;

    public RedisConfiguration(ReactiveRedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @PostConstruct
    public void ensureNotifyEventsEnabled() {
        final var properties = connectionFactory.getReactiveConnection().serverCommands()
            .getConfig("notify-keyspace-events").blockOptional();
        if (properties.isEmpty()) {
            throw new IllegalStateException("notify-keyspace-events not found");
        }
        final var notifications = properties.get().get("notify-keyspace-events").toString();
        if (notifications == null) {
            throw new IllegalStateException("Redis notify-keyspace-events is not enabled. Required: Ex");
        }
        if (!notifications.contains("E") || !notifications.contains("x")) {
            throw new IllegalStateException("Redis notify-keyspace-events is not enabled. (events=" + notifications + ")");
        }
    }

    @Bean
    public StatefulRedisPubSubConnection<String, String> statefulRedisPubSubConnection() {
        if (connectionFactory instanceof LettuceConnectionFactory lettuceConnectionFactory) {
            final var nativeClient = lettuceConnectionFactory.getRequiredNativeClient();
            if (nativeClient instanceof RedisClient redisClient) {
                return redisClient.connectPubSub();
            } else {
                throw new IllegalStateException("NativeClient is not RedisClient");
            }
        } else {
            throw new IllegalStateException("Redis implementation is not Lettuce.");
        }
    }

}