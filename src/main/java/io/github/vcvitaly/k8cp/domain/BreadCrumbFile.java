package io.github.vcvitaly.k8cp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record BreadCrumbFile(PathRefreshEvent event, String name) {

    @Override
    public String toString() {
        return name;
    }
}
