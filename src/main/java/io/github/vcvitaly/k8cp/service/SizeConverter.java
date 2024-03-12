package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.domain.FileSizeContainer;

public interface SizeConverter {

    FileSizeContainer toFileSizeDto(long size);
}
