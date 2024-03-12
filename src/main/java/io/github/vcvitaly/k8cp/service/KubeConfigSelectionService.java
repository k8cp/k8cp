package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.dto.KubeConfigDto;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import java.nio.file.Path;
import java.util.List;

public interface KubeConfigSelectionService {

    List<KubeConfigDto> getConfigChoices(String kubeFolderPath) throws IOOperationException, KubeContextExtractionException;

    KubeConfigDto toConfigDto(Path path) throws KubeContextExtractionException;
}
