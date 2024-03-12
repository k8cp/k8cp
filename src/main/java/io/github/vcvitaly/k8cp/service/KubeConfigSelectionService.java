package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.domain.KubeConfig;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import java.nio.file.Path;
import java.util.List;

public interface KubeConfigSelectionService {

    List<KubeConfig> getConfigChoices(String kubeFolderPath) throws IOOperationException, KubeContextExtractionException;

    KubeConfig toKubeConfig(Path path) throws KubeContextExtractionException;
}
