package io.github.vcvitaly.k8cp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileItemDto {
    private String path;
    private String name;
    private String size;
    private String sizeUnit;
    private String fileType;
    private String changedAt;
}
