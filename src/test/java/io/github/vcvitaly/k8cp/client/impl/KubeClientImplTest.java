package io.github.vcvitaly.k8cp.client.impl;

import io.github.vcvitaly.k8cp.TestUtil;
import io.github.vcvitaly.k8cp.domain.KubeNamespace;
import io.github.vcvitaly.k8cp.domain.KubePod;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class KubeClientImplTest {

    @Container
    private static final K3sContainer K3S = new K3sContainer(DockerImageName.parse("rancher/k3s:v1.27.4-k3s1"));

    private static String podName;

    private final KubeClientImpl kubeClient = new KubeClientImpl(K3S.getKubeConfigYaml());

    @BeforeAll
    static void beforeAll() throws URISyntaxException, IOException, ApiException {
        podName = createNginxPod(K3S).getMetadata().getName();
    }

    @Test
    void getNamespacesTest() throws Exception {
        final List<KubeNamespace> namespaces = kubeClient.getNamespaces();

        assertThat(namespaces)
                .contains(new KubeNamespace("default"));
    }

    @Test
    void getPodsTest() throws Exception {
        final List<KubePod> pods = kubeClient.getPods("default");

        assertThat(pods)
                .contains(new KubePod(podName));
    }

    @Test
    void execAndReturnOutTest() throws Exception {
        final List<String> lines = kubeClient.execAndReturnOut("default", podName, new String[]{"ls", "/"});

        assertThat(lines).contains("root", "home");
    }

    private static ApiClient getClient(K3sContainer k3s) throws IOException {
        final String kubeConfigYaml = k3s.getKubeConfigYaml();
        return Config.fromConfig(new StringReader(kubeConfigYaml));
    }

    private static CoreV1Api getApi(ApiClient client) throws IOException {
        return new CoreV1Api(client);
    }

    private static V1Pod createPod(CoreV1Api api, String namespace, String yamlPath) throws URISyntaxException, IOException, ApiException {
        File file = TestUtil.getFile(yamlPath);
        V1Pod yamlPod = (V1Pod) Yaml.load(file);
        yamlPod.getSpec().setOverhead(null);
        return api.createNamespacedPod(namespace, yamlPod).execute();
    }

    private static V1Pod createNginxPod(K3sContainer k3s) throws URISyntaxException, IOException, ApiException {
        final ApiClient client = getClient(k3s);
        final CoreV1Api coreApi = getApi(client);
        return createPod(coreApi, "default", "/nginx.yml");
    }
}