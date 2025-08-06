package io.github.flux7k.demonstration.adapter.redis.in.mute;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.flux7k.demonstration.adapter.redis.out.mute.RedisMuteCache;
import io.github.flux7k.demonstration.adapter.redis.out.mute.RedisMuteEventPublisher;
import io.github.flux7k.demonstration.application.mute.usecases.MuteEventUseCase;
import io.github.flux7k.demonstration.domain.mute.events.MuteCanceledEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExpiredEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteExtendedEvent;
import io.github.flux7k.demonstration.domain.mute.events.MuteIssuedEvent;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.reactive.ChannelMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Component
public class RedisMuteEventSubscriber {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final StatefulRedisPubSubConnection<String, String> redisPubSubConnection;
    private final MuteEventUseCase muteEventUseCase;
    private final ObjectMapper objectMapper;

    private Disposable expiredSub, canceledSub, extendedSub, issuedSub;

    private final static Logger logger = LoggerFactory.getLogger(RedisMuteEventSubscriber.class);

    private final static String EXPIRED_KEY_EVENT_CHANNEL = "__keyevent@0__:expired";

    private final static String ISSUED_EVENT_CHANNEL = RedisMuteEventPublisher.ISSUED_EVENT_CHANNEL;
    private final static String EXTENDED_EVENT_CHANNEL = RedisMuteEventPublisher.EXTENDED_EVENT_CHANNEL;
    private final static String CANCELED_EVENT_CHANNEL = RedisMuteEventPublisher.CANCELED_EVENT_CHANNEL;

    public RedisMuteEventSubscriber(ReactiveRedisTemplate<String, String> redisTemplate,
                                    StatefulRedisPubSubConnection<String, String> redisPubSubConnection,
                                    MuteEventUseCase muteEventUseCase,
                                    ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.redisPubSubConnection = redisPubSubConnection;
        this.muteEventUseCase = muteEventUseCase;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void subscribeExpiredEvent() {
        this.expiredSub = redisPubSubConnection.reactive()
            .subscribe(EXPIRED_KEY_EVENT_CHANNEL)
            .thenMany(redisPubSubConnection.reactive().observeChannels())
            .map(ChannelMessage::getMessage)
            .filter(message -> message.startsWith(RedisMuteCache.KEY_PREFIX))
            .doOnNext(message -> logger.debug("Received expired key event: {}", message))
            .mapNotNull(message -> message.substring(RedisMuteCache.KEY_PREFIX.length()))
            .handle((String uuidString, SynchronousSink<UUID> sink) -> {
                try {
                    final var parsed = UUID.fromString(uuidString);
                    sink.next(parsed);
                } catch (IllegalArgumentException illegalArgumentException) {
                    logger.error("Failed to parse UUID from expired key event: {}", uuidString, illegalArgumentException);
                }
            })
            .map(MuteExpiredEvent::new)
            .flatMap(event ->
                muteEventUseCase.onMuteExpired(event)
                    .doOnError(throwable -> logger.error("Error processing MuteExpiredEvent: {}", event, throwable))
                    .onErrorResume(throwable -> Mono.empty())
            )
            .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1)).maxBackoff(Duration.ofMinutes(1)))
            .subscribe();
    }

    @PostConstruct
    public void subscribeCanceledEvent() {
        this.canceledSub = redisTemplate
            .listenToChannel(CANCELED_EVENT_CHANNEL)
            .map(ReactiveSubscription.Message::getMessage)
            .doOnNext(message -> logger.debug("Received mute canceled event message: {}", message))
            .flatMap(message -> deserializeEvent(message, MuteCanceledEvent.class))
            .flatMap(event ->
                muteEventUseCase.onMuteCanceled(event)
                    .doOnError(throwable -> logger.error("Error processing MuteCanceledEvent: {}", event, throwable))
                    .onErrorResume(throwable -> Mono.empty())
            )
            .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1)).maxBackoff(Duration.ofMinutes(1)))
            .subscribe();
    }

    @PostConstruct
    public void subscribeExtendedEvent() {
        this.extendedSub = redisTemplate
            .listenToChannel(EXTENDED_EVENT_CHANNEL)
            .map(ReactiveSubscription.Message::getMessage)
            .doOnNext(message -> logger.debug("Received mute extended event message: {}", message))
            .flatMap(message -> deserializeEvent(message, MuteExtendedEvent.class))
            .flatMap(event ->
                muteEventUseCase.onMuteExtended(event)
                    .doOnError(throwable -> logger.error("Error processing MuteExtendedEvent: {}", event, throwable))
                    .onErrorResume(throwable -> Mono.empty())
            )
            .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1)).maxBackoff(Duration.ofMinutes(1)))
            .subscribe();
    }

    @PostConstruct
    public void subscribeIssuedEvent() {
        this.issuedSub = redisTemplate
            .listenToChannel(ISSUED_EVENT_CHANNEL)
            .map(ReactiveSubscription.Message::getMessage)
            .doOnNext(message -> logger.debug("Received mute issued event message: {}", message))
            .flatMap(message -> deserializeEvent(message, MuteIssuedEvent.class))
            .flatMap(event ->
                muteEventUseCase.onMuteIssued(event)
                    .doOnError(throwable -> logger.error("Error processing MuteIssuedEvent: {}", event, throwable))
                    .onErrorResume(throwable -> Mono.empty())
            )
            .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1)).maxBackoff(Duration.ofMinutes(1)))
            .subscribe();
    }

    private <E> Mono<E> deserializeEvent(String message, Class<E> eventClass) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, eventClass))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnError(throwable -> logger.error("Failed to deserialize event: {}", message, throwable))
            .onErrorResume(throwable -> Mono.empty());
    }

    @PreDestroy
    public void teardown() {
        if (expiredSub != null && !expiredSub.isDisposed()) {
            expiredSub.dispose();
        }
        if (canceledSub != null && !canceledSub.isDisposed()) {
            canceledSub.dispose();
        }
        if (extendedSub != null && !extendedSub.isDisposed()) {
            extendedSub.dispose();
        }
        if (issuedSub != null && !issuedSub.isDisposed()) {
            issuedSub.dispose();
        }
    }

}