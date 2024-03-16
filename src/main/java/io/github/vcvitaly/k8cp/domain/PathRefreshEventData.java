package io.github.vcvitaly.k8cp.domain;

import io.github.vcvitaly.k8cp.enumeration.PathRefreshEventSource;
import java.util.UUID;

public record PathRefreshEventData(UUID uuid, String path) {

    public static PathRefreshEventData of(String path) {
        return new PathRefreshEventData(UUID.randomUUID(), path);
    }
}
