package io.github.flux7k.demonstration.domain.mute.events;

import io.github.flux7k.demonstration.domain.mute.Mute;

import java.util.UUID;

public record MuteCanceledEvent(Mute mute, UUID cancelerId) {}