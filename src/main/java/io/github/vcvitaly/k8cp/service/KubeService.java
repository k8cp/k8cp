package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.domain.KubeNamespace;
import io.github.vcvitaly.k8cp.domain.KubePod;
import io.github.vcvitaly.k8cp.exception.KubeApiException;
import java.util.List;

public interface KubeService extends FileService {
    List<KubeNamespace> getNamespaces() throws KubeApiException;

    List<KubePod> getPods(String namespace) throws KubeApiException;
}
