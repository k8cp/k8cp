package io.github.vcvitaly.k8cp.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UnixPathUtil {

    public static String stripEndingSlashFromPath(String path) {
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String concatPaths(String path, String fileName) {
        return "%s/%s".formatted(stripEndingSlashFromPath(path), stripEndingSlashFromPath(fileName));
    }
}
