package io.github.flux7k.demonstration.application.notifier.ports.out;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface Notifier {

    Mono<Void> notify(UUID targetId, String message);

}