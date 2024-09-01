package io.github.vcvitaly.k8cp.domain;

import java.nio.file.Path;
import java.util.UUID;

public record PathRefreshEventData(UUID uuid, Path path) {

    public static PathRefreshEventData of(Path path) {
        return new PathRefreshEventData(UUID.randomUUID(), path);
    }
}
