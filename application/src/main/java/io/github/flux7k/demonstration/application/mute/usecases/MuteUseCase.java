package io.github.flux7k.demonstration.application.mute.usecases;

import io.github.flux7k.demonstration.application.mute.ports.out.MuteCache;
import io.github.flux7k.demonstration.application.mute.ports.out.MuteEventPublisher;
import io.github.flux7k.demonstration.application.mute.ports.out.MuteIdGenerator;
import io.github.flux7k.demonstration.application.mute.ports.out.MuteRepository;
import io.github.flux7k.demonstration.domain.mute.Mute;
import io.github.flux7k.demonstration.domain.mute.MuteService;
import io.github.flux7k.demonstration.domain.mute.events.MuteCanceledEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExtendedEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteIssuedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.util.UUID;

@Service
public class MuteUseCase {

    private final MuteService muteService;
    private final MuteIdGenerator muteIdGenerator;
    private final MuteRepository muteRepository;
    private final MuteCache muteCache;
    private final MuteEventPublisher muteEventPublisher;
    private final Clock clock;

    public MuteUseCase(MuteService muteService,
                       MuteIdGenerator muteIdGenerator,
                       MuteRepository muteRepository,
                       MuteCache muteCache,
                       MuteEventPublisher muteEventPublisher,
                       Clock clock) {
        this.muteService = muteService;
        this.muteIdGenerator = muteIdGenerator;
        this.muteRepository = muteRepository;
        this.muteCache = muteCache;
        this.muteEventPublisher = muteEventPublisher;
        this.clock = clock;
    }

    @Transactional
    public Mono<Mute> issue(UUID targetId, UUID issuerId, String reason, Duration duration) {
        return muteIdGenerator.nextId()
            .map(muteId -> muteService.create(muteId, targetId, issuerId, reason, duration))
            .flatMap(muteRepository::save)
            .flatMap(saved -> {
                Duration ttl = Duration.between(clock.instant(), saved.expiresAt());
                if (ttl.isNegative()) {
                    ttl = Duration.ZERO;
                }
                final var muteIssuedEvent = new MuteIssuedEvent(saved);
                return muteCache.setActive(saved.targetId(), true, ttl)
                    .then(muteEventPublisher.publishMuteIssuedEvent(muteIssuedEvent))
                    .thenReturn(saved);
            });
    }

    @Transactional
    public Mono<Mute> extend(UUID muteId, Duration extra) {
        return muteRepository.findById(muteId)
            .map(mute -> muteService.extend(mute, extra))
            .flatMap(muteRepository::save)
            .flatMap(updated -> {
                Duration ttl = Duration.between(clock.instant(), updated.expiresAt());
                if (ttl.isNegative()) {
                    ttl = Duration.ZERO;
                }
                final var muteExtendedEvent = new MuteExtendedEvent(updated, extra);
                return muteCache.setActive(updated.targetId(), true, ttl)
                    .then(muteEventPublisher.publishMuteExtendedEvent(muteExtendedEvent))
                    .thenReturn(updated);
            });
    }

    @Transactional
    public Mono<Mute> cancel(UUID muteId, UUID cancelerId) {
        return muteRepository.findById(muteId)
            .map(muteService::cancel)
            .flatMap(muteRepository::save)
            .flatMap(updated -> {
                final var muteCanceledEvent = new MuteCanceledEvent(updated, cancelerId);
                return muteCache.evict(updated.targetId())
                    .then(muteEventPublisher.publishMuteCanceledEvent(muteCanceledEvent))
                    .thenReturn(updated);
            });
    }

    public Mono<Boolean> isActive(UUID targetId) {
        return muteCache.getActive(targetId)
            .switchIfEmpty(
                muteRepository.findActiveByTargetId(targetId)
                    .flatMap(mute -> {
                        boolean active = muteService.isActive(mute);
                        if (!active) {
                            return Mono.just(false);
                        }
                        Duration ttl = Duration.between(clock.instant(), mute.expiresAt());
                        if (ttl.isNegative()) {
                            ttl = Duration.ZERO;
                        }
                        return muteCache.setActive(targetId, true, ttl).thenReturn(true);
                    })
                    .defaultIfEmpty(false)
            );
    }

}