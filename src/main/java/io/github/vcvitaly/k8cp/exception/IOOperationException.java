package io.github.vcvitaly.k8cp.exception;

public class IOOperationException extends Exception {

    public IOOperationException(String message) {
        super(message);
    }

    public IOOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IOOperationException(Throwable cause) {
        super(cause);
    }
}
