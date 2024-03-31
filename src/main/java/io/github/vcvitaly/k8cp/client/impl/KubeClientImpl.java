package io.github.vcvitaly.k8cp.client.impl;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.github.vcvitaly.k8cp.client.KubeClient;
import io.github.vcvitaly.k8cp.domain.KubeNamespace;
import io.github.vcvitaly.k8cp.domain.KubePod;
import io.github.vcvitaly.k8cp.exception.KubeApiException;
import io.github.vcvitaly.k8cp.exception.KubeExecException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubeClientImpl implements KubeClient {

    private static final int WAIT_TIMEOUT_MS = 250;
    private static final String UNKNOWN_OBJECT_NAME = "UNKNOWN";
    private KubernetesClient client;

    public KubeClientImpl(String configYml) {
        Config config = Config.fromKubeconfig(configYml);
        client = new KubernetesClientBuilder().withConfig(config).build();
    }

    @Override
    public List<String> execAndReturnOut(String namespace, String podName, String[] cmdParts) throws KubeExecException {
        return null;
    }

    @Override
    public List<KubeNamespace> getNamespaces() throws KubeApiException {
        try {
            return client.namespaces().list().getItems().stream()
                    .map(namespace -> new KubeNamespace(namespace.getMetadata().getName()))
                    .toList();
        } catch (Exception e) {
            throw new KubeApiException("Could not get a list of namespaces", e);
        }
    }

    @Override
    public List<KubePod> getPods(String namespace) throws KubeApiException {
        try {
            return client.pods().inNamespace(namespace).list().getItems().stream()
                    .map(pod -> new KubePod(pod.getMetadata().getName()))
                    .toList();
        } catch (Exception e) {
            throw new KubeApiException("Could not get a list of pods in [%s] namespace".formatted(namespace), e);
        }
    }
}
