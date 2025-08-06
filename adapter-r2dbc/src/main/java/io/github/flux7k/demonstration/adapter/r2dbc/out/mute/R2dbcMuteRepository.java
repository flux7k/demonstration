package io.github.flux7k.demonstration.adapter.r2dbc.out.mute;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface R2dbcMuteRepository extends ReactiveCrudRepository<R2dbcMuteEntity, UUID> {

    Mono<R2dbcMuteEntity> findTopByTargetIdAndActiveIsTrueAndExpiresAtIsAfterOrderByIssuedAtDesc(UUID targetId, OffsetDateTime expiresAtAfter);

}