package io.github.vcvitaly.k8cp.enumeration;

import java.util.Arrays;
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

    public static FileType ofValueName(String valueName) {
        return Arrays.stream(values())
                .filter(v -> v.getValueName().equals(valueName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value name: " + valueName));
    }
}
