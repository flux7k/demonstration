package io.github.flux7k.demonstration.application.mute.ports.out;

import io.github.flux7k.demonstration.domain.mute.events.MuteCanceledEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExpiredEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExtendedEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteIssuedEvent;
import reactor.core.publisher.Mono;

public interface MuteEventPublisher {

    Mono<Void> publishMuteCanceledEvent(MuteCanceledEvent event);

    Mono<Void> publishMuteExtendedEvent(MuteExtendedEvent event);

    Mono<Void> publishMuteIssuedEvent(MuteIssuedEvent event);

    Mono<Void> publishMuteExpiredEvent(MuteExpiredEvent event);

}