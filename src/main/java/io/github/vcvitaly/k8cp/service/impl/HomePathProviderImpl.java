package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.service.HomePathProvider;

public class HomePathProviderImpl implements HomePathProvider {
    private static final String USER_HOME_PROP_NAME = "user.home";

    @Override
    public String provideHomePath() {
        return System.getProperty(USER_HOME_PROP_NAME);
    }
}
