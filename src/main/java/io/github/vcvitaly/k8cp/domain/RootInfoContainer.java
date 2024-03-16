package io.github.vcvitaly.k8cp.domain;

public record RootInfoContainer(PathRefreshEvent event, String name) {

    @Override
    public String toString() {
        return name;
    }
}
