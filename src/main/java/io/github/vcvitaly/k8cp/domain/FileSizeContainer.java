package io.github.vcvitaly.k8cp.domain;

import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;

public record FileSizeContainer(long sizeBytes, int sizeInUnit, FileSizeUnit unit) {
}
