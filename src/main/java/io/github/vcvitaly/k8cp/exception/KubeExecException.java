package io.github.vcvitaly.k8cp.exception;

public class KubeExecException extends Exception {
    public KubeExecException(String message) {
        super(message);
    }

    public KubeExecException(String message, Throwable cause) {
        super(message, cause);
    }

    public KubeExecException(Throwable cause) {
        super(cause);
    }
}
