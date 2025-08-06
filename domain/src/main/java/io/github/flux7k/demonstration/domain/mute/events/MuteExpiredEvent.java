package io.github.flux7k.demonstration.domain.mute.events;

import java.util.UUID;

public record MuteExpiredEvent(UUID targetId) {}