package io.github.vcvitaly.k8cp.util;

import java.nio.file.Path;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtil {

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
}
