package com.weaponlin.inf.prpc.exception;

public class PRpcException extends RuntimeException {
    private static final long serialVersionUID = -8870783014598353543L;

    public PRpcException(String message) {
        super(message);
    }

    public PRpcException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
