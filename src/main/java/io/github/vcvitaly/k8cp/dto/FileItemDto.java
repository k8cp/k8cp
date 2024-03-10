package io.github.vcvitaly.k8cp.dto;

import java.io.File;
import lombok.Builder;
import lombok.Data;

@Data
public class FileItemDto {
    private String path;
    private String name;
    private String size;
    private String fileType;
    private String changedAt;

    @Builder
    public FileItemDto(String path, String name, String size, String sizeUnit, String fileType, String changedAt) {
        this.path = path;
        this.name = name;
        this.size = "%s %s".formatted(size, sizeUnit);
        this.fileType = fileType;
        this.changedAt = changedAt;
    }
}
