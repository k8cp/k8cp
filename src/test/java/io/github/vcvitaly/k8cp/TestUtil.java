package io.github.vcvitaly.k8cp;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtil {

    public static File getFile(String resourcePath) throws URISyntaxException {
        return new File(getUri(resourcePath));
    }

    public static Path getPath(String resourcePath) {
        try {
            return Paths.get(getUri(resourcePath));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static URI getUri(String resourcePath) throws URISyntaxException {
        return TestUtil.class.getResource(resourcePath).toURI();
    }
}
