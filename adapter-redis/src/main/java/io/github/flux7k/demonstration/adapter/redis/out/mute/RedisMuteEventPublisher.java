package io.github.flux7k.demonstration.adapter.redis.out.mute;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.flux7k.demonstration.application.mute.ports.out.MuteEventPublisher;
import io.github.flux7k.demonstration.domain.mute.events.MuteCanceledEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExpiredEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExtendedEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteIssuedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class RedisMuteEventPublisher implements MuteEventPublisher {


    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(RedisMuteEventPublisher.class);

    public static final String CANCELED_EVENT_CHANNEL = "mute:canceled_event:channel";
    public static final String EXTENDED_EVENT_CHANNEL = "mute:extended_event:channel";
    public static final String ISSUED_EVENT_CHANNEL = "mute:issued_event:channel";

    public RedisMuteEventPublisher(ReactiveRedisTemplate<String, String> redisTemplate,
                                   ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> publishMuteCanceledEvent(MuteCanceledEvent event) {
        return serializeEvent(event)
            .flatMap(json -> redisTemplate.convertAndSend(CANCELED_EVENT_CHANNEL, json))
            .doOnError(throwable -> logger.error("Failed to publish MuteCanceledEvent to Redis channel", throwable))
            .then();
    }

    @Override
    public Mono<Void> publishMuteExtendedEvent(MuteExtendedEvent event) {
        return serializeEvent(event)
            .flatMap(json -> redisTemplate.convertAndSend(EXTENDED_EVENT_CHANNEL, json))
            .doOnError(throwable -> logger.error("Failed to publish MuteExtendedEvent to Redis channel", throwable))
            .then();
    }

    @Override
    public Mono<Void> publishMuteIssuedEvent(MuteIssuedEvent event) {
        return serializeEvent(event)
            .flatMap(json -> redisTemplate.convertAndSend(ISSUED_EVENT_CHANNEL, json))
            .doOnError(throwable -> logger.error("Failed to publish MuteIssuedEvent to Redis channel", throwable))
            .then();
    }

    @Override
    public Mono<Void> publishMuteExpiredEvent(MuteExpiredEvent event) {
        throw new UnsupportedOperationException("MuteExpiredEvent publishing is not supported on redis pub/sub.");
    }

    private Mono<String> serializeEvent(Object event) {
        return Mono
            .fromCallable(() -> objectMapper.writeValueAsString(event))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnError(throwable -> logger.error("Failed to serialize event to JSON", throwable));
    }

}