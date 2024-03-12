package io.github.vcvitaly.k8cp.exception;

public class KubeContextExtractionException extends Exception {

    public KubeContextExtractionException(String message) {
        super(message);
    }

    public KubeContextExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public KubeContextExtractionException(Throwable cause) {
        super(cause);
    }
}
