package io.github.vcvitaly.k8cp.dto;

import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;

public record FileSizeDto(long sizeBytes, int sizeInUnit, FileSizeUnit unit) {
}
