package io.github.vcvitaly.k8cp.exception;

public class KubeConfigLoadingException extends Exception {

    public KubeConfigLoadingException(String message) {
        super(message);
    }

    public KubeConfigLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public KubeConfigLoadingException(Throwable cause) {
        super(cause);
    }
}
