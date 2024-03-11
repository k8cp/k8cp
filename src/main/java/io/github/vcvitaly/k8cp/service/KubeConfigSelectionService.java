package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.dto.KubeConfigChoiceDto;
import io.github.vcvitaly.k8cp.exception.FileSystemException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import java.util.List;

public interface KubeConfigSelectionService {

    List<KubeConfigChoiceDto> getConfigChoices(String kubeFolderPath) throws FileSystemException, KubeContextExtractionException;
}
