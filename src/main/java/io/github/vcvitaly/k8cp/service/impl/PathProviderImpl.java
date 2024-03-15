package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.enumeration.OsFamily;
import io.github.vcvitaly.k8cp.service.LocalOsFamilyDetector;
import io.github.vcvitaly.k8cp.service.PathProvider;
import io.github.vcvitaly.k8cp.util.Constants;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathProviderImpl implements PathProvider {
    private static final String USER_HOME_PROP_NAME = "user.home";

    private final LocalOsFamilyDetector localOsFamilyDetector;

    @Override
    public String provideLocalHomePath() {
        return System.getProperty(USER_HOME_PROP_NAME);
    }

    @Override
    public String provideLocalRootPath() {
        final OsFamily osFamily = localOsFamilyDetector.detectOsFamily();
        return switch (osFamily) {
            case WINDOWS -> Constants.WINDOWS_ROOT;
            case LINUX, MACOS -> Constants.UNIX_ROOT;
        };
    }

    @Override
    public String provideRemoteRootPath() {
        return Constants.UNIX_ROOT;
    }
}
