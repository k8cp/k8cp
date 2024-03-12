package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.client.LocalFsClient;
import io.github.vcvitaly.k8cp.dto.KubeConfigSelectionDto;
import io.github.vcvitaly.k8cp.exception.FileSystemException;
import io.github.vcvitaly.k8cp.exception.KubeConfigLoadingException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import io.github.vcvitaly.k8cp.service.KubeConfigSelectionService;
import io.github.vcvitaly.k8cp.service.KubeConfigHelper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class KubeConfigSelectionServiceImpl implements KubeConfigSelectionService {

    private final LocalFsClient localFsClient;
    private final KubeConfigHelper kubeConfigHelper;

    @Override
    public List<KubeConfigSelectionDto> getConfigChoices(String kubeFolderPath) throws FileSystemException, KubeContextExtractionException {
        final List<KubeConfigSelectionDto> list = new ArrayList<>();
        for (Path path : localFsClient.listFiles(kubeFolderPath)) {
            if (kubeConfigHelper.validate(path.toString())) {
                KubeConfigSelectionDto configChoiceDto = toConfigChoiceDto(path);
                list.add(configChoiceDto);
            }
        }
        return list;
    }

    @Override
    public KubeConfigSelectionDto toConfigChoiceDto(Path path) throws KubeContextExtractionException {
        final String pathStr = path.toString();
        return KubeConfigSelectionDto.builder()
                .contextName(getContextName(pathStr))
                .fileName(path.getFileName().toString())
                .path(pathStr)
                .build();
    }

    private String getContextName(String kubeConfigPath) throws KubeContextExtractionException {
        try {
            return kubeConfigHelper.extractContextName(kubeConfigPath);
        } catch (FileSystemException | KubeConfigLoadingException e) {
            throw new KubeContextExtractionException("Could not extract context name from " + kubeConfigPath, e);
        }
    }
}
