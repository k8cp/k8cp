package io.github.vcvitaly.k8cp.client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface LocalFsClient {

    List<Path> listFiles(String path) throws IOException;
}
