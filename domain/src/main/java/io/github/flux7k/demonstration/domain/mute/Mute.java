package io.github.flux7k.demonstration.domain.mute;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Mute(
    @Nonnull UUID id,
    @Nonnull UUID targetId,
    @Nonnull UUID issuerId,
    @Nullable String reason,
    @Nonnull Boolean active,
    @Nonnull OffsetDateTime expiresAt,
    @Nonnull OffsetDateTime issuedAt
) {
}