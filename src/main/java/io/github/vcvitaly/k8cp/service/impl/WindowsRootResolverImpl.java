package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.service.WindowsRootResolver;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class WindowsRootResolverImpl implements WindowsRootResolver {
    @Override
    public List<Path> listLocalRoots() {
        final File[] roots = File.listRoots();
        return Arrays.stream(roots)
                .map(File::toPath)
                .toList();
    }
}
