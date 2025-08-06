package io.github.flux7k.demonstration.application.mute.ports.out;

import io.github.flux7k.demonstration.domain.mute.Mute;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MuteRepository {

    Mono<Mute> save(Mute mute);

    Mono<Mute> findById(UUID id);

    Mono<Mute> findActiveByTargetId(UUID targetId);

}