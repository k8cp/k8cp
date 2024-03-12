package io.github.vcvitaly.k8cp.exception;

public class ParamFieldNotFulfilledException extends RuntimeException {
    public ParamFieldNotFulfilledException(String fieldName) {
        super("Obligatory field %s is not fulfilled".formatted(fieldName));
    }
}
