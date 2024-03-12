package io.github.vcvitaly.k8cp.exception;

public class KubeApiException extends Exception {
    public KubeApiException(String message) {
        super(message);
    }

    public KubeApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public KubeApiException(Throwable cause) {
        super(cause);
    }
}
