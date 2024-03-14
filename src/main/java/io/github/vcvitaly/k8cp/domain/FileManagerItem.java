package io.github.vcvitaly.k8cp.domain;

import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;
import io.github.vcvitaly.k8cp.enumeration.FileType;
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
    public FileManagerItem(String path, String name, Integer size, FileSizeUnit sizeUnit, FileType fileType, String changedAt) {
        this.path = path;
        this.name = name;
        if (fileType == FileType.FILE && size != null && sizeUnit != null) {
            this.size = "%s %s".formatted(size, sizeUnit);
        } else {
            this.size = "";
        }
        this.fileType = fileType.toString();
        this.changedAt = changedAt != null ? changedAt : "";
    }
}
