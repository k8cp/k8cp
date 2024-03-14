package io.github.vcvitaly.k8cp.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FileType {
    FILE("File", 3),
    DIRECTORY("Directory", 2),
    PARENT_DIRECTORY("Parent directory", 1);

    private final String valueName;
    private final int priority; // Smaller better

    @Override
    public String toString() {
        return valueName;
    }
}
