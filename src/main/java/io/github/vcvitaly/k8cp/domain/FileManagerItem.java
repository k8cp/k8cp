package io.github.vcvitaly.k8cp.domain;

import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import java.nio.file.Path;
import lombok.Builder;
import lombok.Data;

@Data
public class FileManagerItem {
    private Path path;
    private String name;
    private SizeHolder size;
    private String fileType;
    private String changedAt;

    @Builder
    public FileManagerItem(Path path, String name, Long sizeBytes, Integer size, FileSizeUnit sizeUnit, FileType fileType,
                           String changedAt) {
        this.path = path;
        this.name = name;
        this.size = fileType == FileType.FILE && size != null && sizeUnit != null ?
                new SizeHolder(sizeBytes, "%s %s".formatted(size, sizeUnit)) :
                new SizeHolder(0L, "");
        this.fileType = fileType.toString();
        this.changedAt = changedAt != null ? changedAt : "";
    }

    @Override
    public String toString() {
        return "FileManagerItem{path=%s, name='%s', size=[%d,%s], fileType='%s', changedAt='%s'}"
                .formatted(path, name, size.sizeBytes(), size.sizeRepresentation(), fileType, changedAt);
    }
}
