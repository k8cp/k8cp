package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.domain.FileInfoContainer;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import java.util.List;

public interface FileService {
    List<FileInfoContainer> listFiles(String namespace, String podName, String path) throws IOOperationException;
}
