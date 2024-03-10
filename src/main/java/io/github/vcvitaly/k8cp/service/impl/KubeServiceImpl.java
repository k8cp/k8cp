package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.client.KubeClient;
import io.github.vcvitaly.k8cp.dto.FileDto;
import io.github.vcvitaly.k8cp.dto.FileSizeDto;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.exception.FileSystemException;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.service.KubeService;
import io.github.vcvitaly.k8cp.service.SizeConverter;
import io.github.vcvitaly.k8cp.util.DateTimeUtil;
import io.github.vcvitaly.k8cp.util.FileUtil;
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
    public List<FileDto> listFiles(String path) throws FileSystemException {
        final ArrayList<String> partsList = new ArrayList<>(LS_PARTS);
        partsList.add("'%s'".formatted(path));
        final String[] cmdParts  = partsList.toArray(String[]::new);
        final String podName = Model.getInstance().getPodName();
        final List<String> lines = kubeClient.execAndReturnOut(podName, cmdParts);
        return lines.stream()
                .map(line -> toFileDto(path, line))
                .toList();
    }

    private FileDto toFileDto(String path, String lsLine) {
        final String[] parts = lsLine.split("\\s+");
        final String attrs = parts[0];
        final long size = Long.parseLong(parts[4]);
        final String date = parts[5];
        final String time = parts[6];
        final String nameRaw = parts[7];
        final String fullPath = FileUtil.concatPaths(path, nameRaw);
        final String name = FileUtil.stripEndingSlashFromPath(nameRaw);
        final FileSizeDto fileSizeDto = sizeConverter.toFileSizeDto(size);
        return FileDto.builder()
                .path(fullPath)
                .name(name)
                .sizeBytes(size)
                .size(fileSizeDto.sizeInUnit())
                .sizeUnit(fileSizeDto.unit())
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
