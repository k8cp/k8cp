package io.github.vcvitaly.k8cp.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    public static String stripEndingSlash(String s) {
        if (s.endsWith("/")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String stripBeginningSlash(String s) {
        if (s.startsWith("/")) {
            return s.substring(1);
        }
        return s;
    }
}
