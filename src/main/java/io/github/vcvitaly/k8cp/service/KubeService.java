package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.domain.FileInfoContainer;
import io.github.vcvitaly.k8cp.domain.KubeNamespace;
import io.github.vcvitaly.k8cp.domain.KubePod;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeApiException;
import java.util.List;

public interface KubeService {

    List<FileInfoContainer> listFiles(String namespace, String podName, String path, boolean showHidden) throws IOOperationException;

    List<KubeNamespace> getNamespaces() throws KubeApiException;

    List<KubePod> getPods(String namespace) throws KubeApiException;
}
