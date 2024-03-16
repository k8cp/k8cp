package io.github.vcvitaly.k8cp.domain;

import io.github.vcvitaly.k8cp.enumeration.PathRefreshEventSource;
import java.util.Comparator;
import java.util.UUID;

public record PathRefreshEvent(PathRefreshEventSource source, PathRefreshEventData data) {

    public static PathRefreshEvent of(PathRefreshEventSource source, String path) {
        return new PathRefreshEvent(source, PathRefreshEventData.of(path));
    }

    public boolean equalsByData(PathRefreshEvent otherEvent) {
        return Comparator
                .comparing((PathRefreshEvent event) -> event.data().uuid())
                .thenComparing((PathRefreshEvent event) -> event.data().path())
                .compare(this, otherEvent) == 0;
    }
}
