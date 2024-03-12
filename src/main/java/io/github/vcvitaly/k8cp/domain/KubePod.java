package io.github.vcvitaly.k8cp.domain;

import lombok.Builder;

@Builder
public record KubePod(String name) {

    @Override
    public String toString() {
        return name;
    }
}
