package io.github.vcvitaly.k8cp.dto;

import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDto {
    private String path;
    private String name;
    private long sizeBytes;
    private int size;
    private FileSizeUnit sizeUnit;
    private FileType fileType;
    private LocalDateTime changedAt;
}
