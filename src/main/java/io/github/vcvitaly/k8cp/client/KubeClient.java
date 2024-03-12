package io.github.vcvitaly.k8cp.client;

import io.github.vcvitaly.k8cp.domain.KubeNamespace;
import io.github.vcvitaly.k8cp.exception.KubeApiException;
import io.github.vcvitaly.k8cp.exception.KubeExecException;
import java.util.List;

public interface KubeClient {

    List<String> execAndReturnOut(String namespace, String podName, String[] cmdParts) throws KubeExecException;

    List<KubeNamespace> getNamespaces() throws KubeApiException;
}
