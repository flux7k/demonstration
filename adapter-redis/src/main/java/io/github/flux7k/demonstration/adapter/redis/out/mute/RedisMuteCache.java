package io.github.flux7k.demonstration.adapter.redis.out.mute;

import io.github.flux7k.demonstration.application.mute.ports.out.MuteCache;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Component
public class RedisMuteCache implements MuteCache {

    public static final String KEY_PREFIX = "mute:active:";

    private final ReactiveRedisTemplate<String, Boolean> redisTemplate;

    public RedisMuteCache(ReactiveRedisTemplate<String, Boolean> reactiveStringRedisTemplate) {
        this.redisTemplate = reactiveStringRedisTemplate;
    }

    private static String key(UUID targetId) {
        return KEY_PREFIX + targetId;
    }

    @Override
    public Mono<Boolean> getActive(UUID targetId) {
        return redisTemplate.opsForValue()
            .get(key(targetId))
            .switchIfEmpty(Mono.empty()); // represents cache miss
    }

    @Override
    public Mono<Void> setActive(UUID targetId, boolean active, Duration ttl) {
        return redisTemplate.opsForValue()
            .set(key(targetId), active, ttl)
            .then();
    }

    @Override
    public Mono<Void> evict(UUID targetId) {
        return redisTemplate.unlink(key(targetId)).then();
    }

}