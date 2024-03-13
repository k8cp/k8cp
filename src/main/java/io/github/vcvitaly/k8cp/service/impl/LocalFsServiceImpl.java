package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.client.LocalFsClient;
import io.github.vcvitaly.k8cp.domain.FileInfoContainer;
import io.github.vcvitaly.k8cp.domain.FileSizeContainer;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.service.LocalFsService;
import io.github.vcvitaly.k8cp.service.SizeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocalFsServiceImpl implements LocalFsService {

    private final LocalFsClient localFsClient;
    private final SizeConverter sizeConverter;

    @Override
    public List<FileInfoContainer> listFiles(String path) throws IOOperationException {
        return listFilesInternal(path);
    }

    private List<FileInfoContainer> listFilesInternal(String path) throws IOOperationException {
        final List<Path> paths = localFsClient.listFiles(path);
        final List<FileInfoContainer> list = new ArrayList<>();
        for (Path p : paths) {
            final FileInfoContainer fileInfoContainer = toFileDto(p);
            list.add(fileInfoContainer);
        }
        return list;
    }

    private FileInfoContainer toFileDto(Path path) throws IOOperationException {
        try {
            final long size = Files.size(path);
            final FileSizeContainer fileSizeContainer = sizeConverter.toFileSizeDto(size);
            final BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            return FileInfoContainer.builder()
                    .path(path.toString())
                    .name(path.getFileName().toString())
                    .sizeBytes(size)
                    .size(fileSizeContainer.sizeInUnit())
                    .sizeUnit(fileSizeContainer.unit())
                    .fileType(Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE)
                    .changedAt(toLocalDateTime(attrs.lastModifiedTime()))
                    .build();
        } catch (IOException e) {
            throw new IOOperationException("An error while reading attributes for " + path, e);
        }
    }

    private LocalDateTime toLocalDateTime(FileTime fileTime) {
        return fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
