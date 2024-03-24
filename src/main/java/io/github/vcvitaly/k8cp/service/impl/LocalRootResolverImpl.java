package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.enumeration.OsFamily;
import io.github.vcvitaly.k8cp.service.LocalRootResolver;
import io.github.vcvitaly.k8cp.util.Constants;
import io.github.vcvitaly.k8cp.util.LocalFileUtil;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Override
    public RootInfoContainer getMainRoot(OsFamily osFamily) {
        return switch (osFamily) {
            case WINDOWS -> new RootInfoContainer(
                    Constants.WINDOWS_ROOT,
                    LocalFileUtil.normalizeRootPath(Paths.get(Constants.WINDOWS_ROOT))
            );
            case LINUX, MACOS -> new RootInfoContainer(Constants.UNIX_ROOT, Constants.UNIX_ROOT);
        };
    }
}
