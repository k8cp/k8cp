package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.dto.FileSizeDto;

public interface SizeConverter {

    FileSizeDto toFileSizeDto(long size);
}
