package io.github.flux7k.demonstration.application.mute.ports.in;

import io.github.flux7k.demonstration.domain.mute.events.MuteCanceledEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExpiredEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExtendedEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteIssuedEvent;
import reactor.core.publisher.Mono;

public interface MuteEventPort {

    Mono<Void> onMuteIssued(MuteIssuedEvent event);

    Mono<Void> onMuteExtended(MuteExtendedEvent event);

    Mono<Void> onMuteExpired(MuteExpiredEvent event);

    Mono<Void> onMuteCanceled(MuteCanceledEvent event);

}