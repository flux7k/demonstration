package io.github.flux7k.demonstration.domain.mute;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MuteService {

    private final MutePolicy mutePolicy;
    private final Clock clock;

    public MuteService(MutePolicy mutePolicy, Clock clock) {
        this.mutePolicy = mutePolicy;
        this.clock = clock;
    }

    public @Nonnull Mute create(@Nonnull UUID id,
                                @Nonnull UUID targetId,
                                @Nonnull UUID issuerId,
                                @Nullable String reason,
                                @Nonnull Duration duration) {
        final var now = now();
        final var normalizedReason = mutePolicy.normalizeReason(reason);
        mutePolicy.validateNew(duration, normalizedReason);
        return new Mute(
            id,
            targetId,
            issuerId,
            normalizedReason,
            true,
            now.plus(duration),
            now
        );
    }

    public boolean isActive(@Nonnull Mute mute) {
        return mute.active() && mute.expiresAt().isAfter(now());
    }

    public @Nonnull Mute extend(@Nonnull Mute mute, @Nonnull Duration extra) {
        mutePolicy.validateExtend(mute, extra);
        final var newExpiresAt = mute.expiresAt().plus(extra);
        mutePolicy.validateExpireTimeWindow(mute.issuedAt(), newExpiresAt);
        return new Mute(
            mute.id(),
            mute.targetId(),
            mute.issuerId(),
            mute.reason(),
            true,
            newExpiresAt,
            mute.issuedAt()
        );
    }

    public @Nonnull Mute cancel(@Nonnull Mute mute) {
        if (!mute.active()) {
            return mute;
        }
        return new Mute(
            mute.id(),
            mute.targetId(),
            mute.issuerId(),
            mute.reason(),
            false,
            mute.expiresAt(),
            mute.issuedAt()
        );
    }

    public @Nonnull Mute updateReason(@Nonnull Mute mute, @Nullable String reason) {
        final var normalizedReason = mutePolicy.normalizeReason(reason);
        mutePolicy.validateReason(normalizedReason);
        return new Mute(
            mute.id(),
            mute.targetId(),
            mute.issuerId(),
            normalizedReason,
            mute.active(),
            mute.expiresAt(),
            mute.issuedAt()
        );
    }

    private OffsetDateTime now() {
        return OffsetDateTime.now(clock);
    }

}
