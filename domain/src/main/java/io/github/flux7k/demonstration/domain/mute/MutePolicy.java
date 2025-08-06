package io.github.flux7k.demonstration.domain.mute;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Duration;
import java.time.OffsetDateTime;

public interface MutePolicy {

    void validateNew(@Nonnull Duration duration, @Nullable String reason);

    void validateExtend(@Nonnull Mute mute, @Nonnull Duration extra);

    void validateExpireTimeWindow(@Nonnull OffsetDateTime issuedAt, @Nonnull OffsetDateTime expiresAt);

    void validateReason(@Nullable String reason);

    @Nullable String normalizeReason(@Nullable String reason);

}
