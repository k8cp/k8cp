package io.github.vcvitaly.k8cp.domain;

public record KubeNamespace(String name) {

    @Override
    public String toString() {
        return name;
    }
}
