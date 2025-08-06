package io.github.flux7k.demonstration.application.mute.ports.out;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

public interface MuteCache {

    Mono<Boolean> getActive(UUID targetId);

    Mono<Void> setActive(UUID targetId, boolean active, Duration ttl);

    Mono<Void> evict(UUID targetId);

}