package io.github.vcvitaly.k8cp.util;

import java.nio.file.Path;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtil {

    public static boolean shouldBeShownBasedOnHiddenFlag(Path p, boolean showHidden) {
        if (showHidden) {
            return true;
        }
        return !p.getFileName().toString().startsWith(".") && !p.toFile().isHidden();
    }
}
