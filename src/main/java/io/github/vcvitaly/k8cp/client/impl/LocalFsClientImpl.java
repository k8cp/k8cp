package io.github.vcvitaly.k8cp.client.impl;

import io.github.vcvitaly.k8cp.client.LocalFsClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class LocalFsClientImpl implements LocalFsClient {
    @Override
    public List<Path> listFiles(String pathStr) throws IOException {
        final Path path = Paths.get(pathStr);
        try (final Stream<Path> pathStream = Files.list(path)) {
            return pathStream.toList();
        }
    }
}
