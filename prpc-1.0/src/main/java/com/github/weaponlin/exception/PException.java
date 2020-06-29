package com.github.weaponlin.exception;

public class PException extends RuntimeException {
    private static final long serialVersionUID = -8870783014598353543L;

    private static final String FORMAT = "Request requestId: %s, serviceName: %s, methodName: %s, Response requestId: %s";

    public PException(String message) {
        super(message);
    }

    public PException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
