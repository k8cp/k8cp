package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeConfigLoadingException;

public interface KubeConfigHelper {

    boolean validate(String path) throws IOOperationException;

    String extractContextName(String path) throws IOOperationException, KubeConfigLoadingException;
}
