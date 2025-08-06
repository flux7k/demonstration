package io.github.flux7k.demonstration.domain.mute.events;

import io.github.flux7k.demonstration.domain.mute.Mute;

import java.time.Duration;

public record MuteExtendedEvent(Mute mute, Duration duration) {}