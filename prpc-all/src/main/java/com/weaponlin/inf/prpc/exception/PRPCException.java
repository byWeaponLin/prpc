package com.weaponlin.inf.prpc.exception;

public class PRPCException extends RuntimeException {
    private static final long serialVersionUID = -8870783014598353543L;

    public PRPCException(String message) {
        super(message);
    }

    public PRPCException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
