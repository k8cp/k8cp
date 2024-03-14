package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.domain.FileInfoContainer;
import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import java.util.List;

public interface LocalFsService {

    List<FileInfoContainer> listFiles(String path, boolean showHidden) throws IOOperationException;

    List<RootInfoContainer> listWindowsRoots();

    List<RootInfoContainer> listLinuxRoots() throws IOOperationException;

    List<RootInfoContainer> listMacosRoots() throws IOOperationException;
}
