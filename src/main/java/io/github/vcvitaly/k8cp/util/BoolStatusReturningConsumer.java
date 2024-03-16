package io.github.vcvitaly.k8cp.util;

import java.util.function.Predicate;

@FunctionalInterface
public interface BoolStatusReturningConsumer<T> extends Predicate<T> {
    default boolean accept(T t) {
        return test(t);
    }
}
