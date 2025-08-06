package io.github.flux7k.demonstration.domain.mute;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Duration;
import java.time.OffsetDateTime;

final public class DefaultMutePolicy implements MutePolicy {

    private static final Duration MAX_DURATION = Duration.ofDays(30);
    private static final Duration MIN_DURATION = Duration.ofMinutes(1);
    private static final int MAX_REASON_LENGTH = 256;

    @Override
    public void validateNew(@Nonnull Duration duration, @Nullable String reason) {
        if (duration.isNegative() || duration.isZero()) {
            throw new MuteValidationException("Duration must be positive");
        }
        if (duration.compareTo(MIN_DURATION) < 0) {
            throw new MuteValidationException("Duration too short (min=" + MIN_DURATION + ")");
        }
        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new MuteValidationException("Duration too long (max=" + MAX_DURATION + ")");
        }
        validateReason(reason);
    }

    @Override
    public void validateExtend(@Nonnull Mute mute, @Nonnull Duration extra) {
        if (extra.isNegative() || extra.isZero()) {
            throw new MuteValidationException("Extra duration must be positive");
        }
        if (!mute.active()) {
            throw new MuteValidationException("Cannot extend inactive mute");
        }
        if (extra.compareTo(MAX_DURATION) > 0) {
            throw new MuteValidationException("Extra duration too long (max=" + MAX_DURATION + ")");
        }
    }

    @Override
    public void validateExpireTimeWindow(@Nonnull OffsetDateTime issuedAt, @Nonnull OffsetDateTime expiresAt) {
        if (expiresAt.isBefore(issuedAt)) {
            throw new MuteValidationException("Expire time must be after issued time");
        }
    }

    @Override
    public void validateReason(@Nullable String reason) {
        if (reason != null && reason.length() > MAX_REASON_LENGTH) {
            throw new MuteValidationException("Reason too long (max=" + MAX_REASON_LENGTH + ")");
        }
    }

    @Override
    public @Nullable String normalizeReason(@Nullable String reason) {
        if (reason == null || reason.isBlank()) {
            return null;
        }
        String trimmed = reason.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }

}
