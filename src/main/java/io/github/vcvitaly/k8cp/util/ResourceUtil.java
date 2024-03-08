package io.github.vcvitaly.k8cp.util;

import java.net.URL;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourceUtil {

    public static URL getResource(String resourcePath) {
        return ResourceUtil.class.getResource(resourcePath);
    }
}
