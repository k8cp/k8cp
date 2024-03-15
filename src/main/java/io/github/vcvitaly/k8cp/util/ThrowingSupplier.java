package io.github.vcvitaly.k8cp.util;

public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
