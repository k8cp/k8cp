package io.github.vcvitaly.k8cp.util;

import io.github.vcvitaly.k8cp.exception.IOOperationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathUtil {

    public static boolean shouldBeShownBasedOnHiddenFlag(Path p, boolean showHidden) throws IOOperationException {
        if (showHidden) {
            return true;
        }
        if (p.equals(getRoot(p))) {
            return true;
        }
        try {
            return !p.getFileName().toString().startsWith(".") && !Files.isHidden(p);
        } catch (IOException e) {
            throw new IOOperationException("Cannot verify if %s is hidden".formatted(p), e);
        }
    }

    public static boolean isRoot(Path p) {
        return p.getParent() == null;
    }

    public static String normalizeRootPath(Path root) {
        return root.toString()
                .replace(Constants.WINDOWS_DRIVE_LETTER_SUFFIX, "")
                .replace(Constants.WINDOWS_SEPARATOR, Constants.UNIX_SEPARATOR);
    }

    public static String getPathFilename(Path path) {
        return isRoot(path) ? normalizeRootPath(path) : path.getFileName().toString();
    }

    public static boolean isInTheSameRoot(Path path1, Path path2) {
        return getRoot(path1).equals(getRoot(path2));
    }

    public static Path getRoot(Path path1) {
        return path1.getRoot();
    }

    public static Path concatPaths(Path p1, Path p2) {
        return p1.resolve(p2);
    }

    public static Path getPath(String path) {
        return Paths.get(path);
    }

    public static Path getParent(Path path) {
        return path.getParent();
    }
}
