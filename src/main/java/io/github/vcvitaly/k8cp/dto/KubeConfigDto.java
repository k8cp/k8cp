package io.github.vcvitaly.k8cp.dto;

import lombok.Builder;

@Builder
public record KubeConfigDto(String contextName, String fileName, String path) {
    @Override
    public String toString() {
        return "%s - %s".formatted(contextName, fileName);
    }
}
