package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.dto.KubeConfigSelectionDto;
import io.github.vcvitaly.k8cp.exception.FileSystemException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import java.util.List;

public interface KubeConfigSelectionService {

    List<KubeConfigSelectionDto> getConfigChoices(String kubeFolderPath) throws FileSystemException, KubeContextExtractionException;
}
