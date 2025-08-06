package io.github.flux7k.demonstration.application.mute.usecases;

import io.github.flux7k.demonstration.application.mute.ports.in.MuteEventPort;
import io.github.flux7k.demonstration.application.notifier.ports.out.Notifier;
import io.github.flux7k.demonstration.domain.mute.events.MuteCanceledEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExpiredEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExtendedEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteIssuedEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;

@Service
public class MuteEventUseCase implements MuteEventPort {

    private final Notifier notifier;
    private final DateTimeFormatter dateTimeFormatter;

    public MuteEventUseCase(
        Notifier notifier,
        DateTimeFormatter dateTimeFormatter
    ) {
        this.notifier = notifier;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Mono<Void> onMuteIssued(MuteIssuedEvent event) {
        final var formattedExpiry = event.mute().expiresAt().format(dateTimeFormatter);
        return Mono.whenDelayError(
            notifier.notify(event.mute().targetId(), "You have been muted by user " + event.mute().issuerId() + " until " + formattedExpiry + ". Reason: " + event.mute().reason()),
            notifier.notify(event.mute().issuerId(), "You have muted user " + event.mute().targetId() + " until " + formattedExpiry + ". Reason: " + event.mute().reason())
        );
    }

    @Override
    public Mono<Void> onMuteExtended(MuteExtendedEvent event) {
        final var formattedExpiry = event.mute().expiresAt().format(dateTimeFormatter);
        return Mono.whenDelayError(
            notifier.notify(event.mute().targetId(), "Your mute has been extended by user " + event.mute().issuerId() + " until " + formattedExpiry + ". Reason: " + event.mute().reason()),
            notifier.notify(event.mute().issuerId(), "You have extended the mute of user " + event.mute().targetId() + " until " + formattedExpiry + ". Reason: " + event.mute().reason())
        );
    }

    @Override
    public Mono<Void> onMuteExpired(MuteExpiredEvent event) {
        return notifier.notify(event.mute().targetId(), "Your mute has expired.");
    }

    @Override
    public Mono<Void> onMuteCanceled(MuteCanceledEvent event) {
        return Mono.whenDelayError(
            notifier.notify(event.mute().targetId(), "Your mute has been canceled by user " + event.cancelerId() + "."),
            notifier.notify(event.cancelerId(), "You have canceled the mute of user " + event.mute().targetId() + ".")
        );
    }

}