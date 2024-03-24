package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.enumeration.OsFamily;
import java.nio.file.Path;
import java.util.List;

public interface LocalRootResolver {
    List<Path> listWindowsRoots();

    RootInfoContainer getMainRoot(OsFamily osFamily);
}
