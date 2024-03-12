package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.client.KubeClient;
import io.github.vcvitaly.k8cp.domain.FileInfoContainer;
import io.github.vcvitaly.k8cp.domain.FileSizeContainer;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeExecException;
import io.github.vcvitaly.k8cp.service.KubeService;
import io.github.vcvitaly.k8cp.service.SizeConverter;
import io.github.vcvitaly.k8cp.util.DateTimeUtil;
import io.github.vcvitaly.k8cp.util.UnixPathUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KubeServiceImpl implements KubeService {

    private static final List<String> LS_PARTS = List.of("ls", "--time-style=long-iso", "-l");
    private static final String DIRECTORY_MODIFIER = "d";

    private final KubeClient kubeClient;
    private final SizeConverter sizeConverter;

    @Override
    public List<FileInfoContainer> listFiles(String namespace, String podName, String path) throws IOOperationException {
        final ArrayList<String> partsList = new ArrayList<>(LS_PARTS);
        partsList.add("'%s'".formatted(path));
        final String[] cmdParts  = partsList.toArray(String[]::new);
        try {
            final List<String> lines = kubeClient.execAndReturnOut(namespace, podName, cmdParts);
            return lines.stream()
                    .map(line -> toFileInfoContainer(path, line))
                    .toList();
        } catch (KubeExecException e) {
            throw new IOOperationException("Could not get a list of files at [%s@%s]".formatted(podName, path), e);
        }
    }

    private FileInfoContainer toFileInfoContainer(String path, String lsLine) {
        final String[] parts = lsLine.split("\\s+");
        final String attrs = parts[0];
        final long size = Long.parseLong(parts[4]);
        final String date = parts[5];
        final String time = parts[6];
        final String nameRaw = parts[7];
        final String fullPath = UnixPathUtil.concatPaths(path, nameRaw);
        final String name = UnixPathUtil.stripEndingSlashFromPath(nameRaw);
        final FileSizeContainer fileSizeContainer = sizeConverter.toFileSizeDto(size);
        return FileInfoContainer.builder()
                .path(fullPath)
                .name(name)
                .sizeBytes(size)
                .size(fileSizeContainer.sizeInUnit())
                .sizeUnit(fileSizeContainer.unit())
                .fileType(getType(attrs))
                .changedAt(DateTimeUtil.toLocalDate(date, time))
                .build();
    }

    private FileType getType(String attrs) {
        if (attrs.startsWith(DIRECTORY_MODIFIER)) {
            return FileType.DIRECTORY;
        }
        return FileType.FILE;
    }
}
