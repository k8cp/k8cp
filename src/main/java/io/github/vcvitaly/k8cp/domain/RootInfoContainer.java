package io.github.vcvitaly.k8cp.domain;

public record RootInfoContainer(String path, String name) {

    @Override
    public String toString() {
        return name;
    }
}
