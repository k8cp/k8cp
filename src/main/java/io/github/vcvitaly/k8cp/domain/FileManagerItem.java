package io.github.vcvitaly.k8cp.domain;

import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;
import lombok.Builder;
import lombok.Data;

@Data
public class FileManagerItem {
    private String path;
    private String name;
    private String size;
    private String fileType;
    private String changedAt;

    @Builder
    public FileManagerItem(String path, String name, Integer size, FileSizeUnit sizeUnit, String fileType, String changedAt) {
        this.path = path;
        this.name = name;
        if (size != null && sizeUnit != null) {
            this.size = "%s %s".formatted(size, sizeUnit);
        } else {
            this.size = "";
        }
        this.fileType = fileType;
        this.changedAt = changedAt != null ? changedAt : "";
    }
}
