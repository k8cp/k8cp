package io.github.vcvitaly.k8cp.service;

import java.nio.file.Path;
import java.util.List;

public interface WindowsRootResolver {
    List<Path> listLocalRoots();
}
