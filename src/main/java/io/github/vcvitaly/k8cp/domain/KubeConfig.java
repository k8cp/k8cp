package io.github.vcvitaly.k8cp.domain;

import lombok.Builder;

@Builder
public record KubeConfig(String contextName, String fileName, String path) {
    @Override
    public String toString() {
        return "%s - %s".formatted(contextName, fileName);
    }
}
