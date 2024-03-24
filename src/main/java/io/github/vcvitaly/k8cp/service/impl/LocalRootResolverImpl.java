package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.service.LocalRootResolver;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class LocalRootResolverImpl implements LocalRootResolver {
    @Override
    public List<Path> listWindowsRoots() {
        final File[] roots = File.listRoots();
        return Arrays.stream(roots)
                .map(File::toPath)
                .toList();
    }
}
