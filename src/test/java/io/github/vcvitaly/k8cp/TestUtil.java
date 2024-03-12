package io.github.vcvitaly.k8cp;

import java.io.File;
import java.net.URISyntaxException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtil {

    public static File getFile(String resourcePath) throws URISyntaxException {
        return new File(TestUtil.class.getResource(resourcePath).toURI());
    }
}
