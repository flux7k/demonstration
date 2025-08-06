package io.github.flux7k.demonstration.application.mute.ports.out;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MuteIdGenerator {

    Mono<UUID> nextId();

}
