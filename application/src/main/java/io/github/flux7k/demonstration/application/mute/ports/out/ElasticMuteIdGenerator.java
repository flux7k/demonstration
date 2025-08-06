package io.github.flux7k.demonstration.application.mute.ports.out;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Component
final class ElasticMuteIdGenerator implements MuteIdGenerator {

    public Mono<UUID> nextId() {
        return Mono.fromCallable(UUID::randomUUID).subscribeOn(Schedulers.boundedElastic());
    }

}