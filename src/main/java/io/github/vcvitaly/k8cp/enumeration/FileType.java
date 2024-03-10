package io.github.vcvitaly.k8cp.enumeration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FileType {
    FILE("File"),
    DIRECTORY("Directory"),
    PARENT_DIRECTORY("Parent directory");

    private final String valueName;

    @Override
    public String toString() {
        return valueName;
    }
}
