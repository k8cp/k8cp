package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.enumeration.OsFamily;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import java.nio.file.Path;
import java.util.List;

public interface LocalRootResolver {
    List<Path> listWindowsRoots();

    List<RootInfoContainer> listLinuxRoots() throws IOOperationException;

    List<RootInfoContainer> listMacosRoots() throws IOOperationException;

    RootInfoContainer getMainRoot(OsFamily osFamily);
}
