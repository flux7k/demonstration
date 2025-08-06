package io.github.flux7k.demonstration.domain;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
