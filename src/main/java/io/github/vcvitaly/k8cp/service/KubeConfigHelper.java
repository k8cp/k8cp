package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.exception.FileSystemException;
import io.github.vcvitaly.k8cp.exception.KubeConfigLoadingException;

public interface KubeConfigHelper {

    boolean validate(String path) throws FileSystemException;

    String extractContextName(String path) throws FileSystemException, KubeConfigLoadingException;
}
