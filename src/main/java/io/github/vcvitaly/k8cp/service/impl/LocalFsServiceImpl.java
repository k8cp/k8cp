package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.client.LocalFsClient;
import io.github.vcvitaly.k8cp.dto.FileDto;
import io.github.vcvitaly.k8cp.dto.FileSizeDto;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.exception.FileSystemException;
import io.github.vcvitaly.k8cp.service.LocalFsService;
import io.github.vcvitaly.k8cp.service.SizeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocalFsServiceImpl implements LocalFsService {

    private final LocalFsClient localFsClient;
    private final SizeConverter sizeConverter;

    @Override
    public List<FileDto> listFiles(String path) throws FileSystemException {
        try {
            return listFilesInternal(path);
        } catch (IOException e) {
            throw new FileSystemException(e);
        }
    }

    private List<FileDto> listFilesInternal(String path) throws IOException {
        final List<Path> paths = localFsClient.listFiles(path);
        return paths.stream()
                .map(this::toFileDto)
                .toList();
    }

    private FileDto toFileDto(Path path) {
        try {
            final long size = Files.size(path);
            final FileSizeDto fileSizeDto = sizeConverter.toFileSizeDto(size);
            final BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            return FileDto.builder()
                    .path(path.toString())
                    .name(path.getFileName().toString())
                    .sizeBytes(size)
                    .size(fileSizeDto.sizeInUnit())
                    .sizeUnit(fileSizeDto.unit())
                    .fileType(Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE)
                    .changedAt(toLocalDateTime(attrs.lastModifiedTime()))
                    .build();
        } catch (IOException e) {
            throw new FileSystemException(e);
        }
    }

    private LocalDateTime toLocalDateTime(FileTime fileTime) {
        return fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
