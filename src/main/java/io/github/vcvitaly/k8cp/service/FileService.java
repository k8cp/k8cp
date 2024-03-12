package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.dto.FileDto;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import java.util.List;

public interface FileService {
    List<FileDto> listFiles(String namespace, String podName, String path) throws IOOperationException;
}
