package io.github.flux7k.demonstration.adapter.r2dbc.out.mute;

import io.github.flux7k.demonstration.application.mute.ports.out.MuteRepository;
import io.github.flux7k.demonstration.domain.mute.Mute;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public class R2dbcMuteRepositoryAdapter implements MuteRepository {

    private final R2dbcMuteRepository delegate;
    private final Clock clock;

    public R2dbcMuteRepositoryAdapter(R2dbcMuteRepository delegate, Clock clock) {
        this.delegate = delegate;
        this.clock = clock;
    }

    @Override
    public Mono<Mute> save(Mute mute) {
        final var entity = mapToR2dbcEntity(mute);
        return delegate
            .save(entity)
            .map(R2dbcMuteEntity::mapToDomain);
    }

    @Override
    public Mono<Mute> findById(UUID id) {
        return delegate
            .findById(id)
            .map(R2dbcMuteEntity::mapToDomain);
    }

    @Override
    public Mono<Mute> findActiveByTargetId(UUID targetId) {
        return delegate
            .findTopByTargetIdAndActiveIsTrueAndExpiresAtIsAfterOrderByIssuedAtDesc(targetId, OffsetDateTime.now(clock))
            .map(R2dbcMuteEntity::mapToDomain);
    }

    private R2dbcMuteEntity mapToR2dbcEntity(Mute mute) {
        return new R2dbcMuteEntity(
                mute.id(),
                mute.targetId(),
                mute.issuerId(),
                mute.reason(),
                mute.active(),
                mute.expiresAt(),
                mute.issuedAt()
        );
    }

}
