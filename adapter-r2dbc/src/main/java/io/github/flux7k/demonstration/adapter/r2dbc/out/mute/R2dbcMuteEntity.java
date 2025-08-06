package io.github.flux7k.demonstration.adapter.r2dbc.out.mute;

import io.github.flux7k.demonstration.domain.mute.Mute;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("mute")
public record R2dbcMuteEntity(
    @Id UUID id,
    UUID targetId,
    UUID issuerId,
    String reason,
    boolean active,
    OffsetDateTime expiresAt,
    OffsetDateTime issuedAt
) {

    public Mute mapToDomain() {
        return new Mute(
            this.id,
            this.targetId,
            this.issuerId,
            this.reason,
            this.active,
            this.expiresAt,
            this.issuedAt
        );
    }

}