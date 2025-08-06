package io.github.flux7k.demonstration.domain.mute;

import io.github.flux7k.demonstration.domain.DomainException;

public class MuteValidationException extends DomainException {

    public MuteValidationException(String message) {
        super(message);
    }

}