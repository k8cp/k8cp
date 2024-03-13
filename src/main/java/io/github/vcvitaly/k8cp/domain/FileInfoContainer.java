package io.github.vcvitaly.k8cp.domain;

import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInfoContainer {
    private String path;
    private String name;
    private Long sizeBytes;
    private Integer size;
    private FileSizeUnit sizeUnit;
    private FileType fileType;
    private LocalDateTime changedAt;
}
