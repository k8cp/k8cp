package io.github.vcvitaly.k8cp.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LocalFileUtil {

    public static boolean shouldBeShownBasedOnHiddenFlag(Path p, boolean showHidden) {
        if (showHidden) {
            return true;
        }
        if (p.equals(p.getRoot())) {
            return true;
        }
        return !p.getFileName().toString().startsWith(".") && !p.toFile().isHidden();
    }

    public static boolean isRoot(Path p) {
        return p.getParent() == null;
    }

    public static String normalizeRootPath(Path root) {
        return root.toString().replace(Constants.WINDOWS_DRIVE_LETTER_SUFFIX, "");
    }

    public static String getPathFilename(Path path) {
        return isRoot(path) ? normalizeRootPath(path) : path.getFileName().toString();
    }

    public static boolean isInTheSameRoot(String path1Str, String path2Str) {
        final Path path1 = Paths.get(path1Str);
        final Path path2 = Paths.get(path2Str);
        return path1.getRoot().equals(path2.getRoot());
    }
}
