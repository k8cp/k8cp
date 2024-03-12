package io.github.vcvitaly.k8cp.client;

import java.util.List;

public interface KubeClient {

    List<String> execAndReturnOut(String namespace, String podName, String[] cmdParts);
}
