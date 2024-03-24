package io.github.vcvitaly.k8cp.client.impl;

import io.github.vcvitaly.k8cp.TestUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.zeroturnaround.zip.ZipUtil;

import static org.assertj.core.api.Assertions.assertThat;

class LocalFsClientImplTest {

    private final LocalFsClientImpl localFsClient = new LocalFsClientImpl();

    @Test
    void listsFilesTest() throws Exception {
        final Path testFsPath = Files.createTempDirectory("test_fs");
        ZipUtil.unpack(TestUtil.getFile("/test_fs_1.zip"), testFsPath.toFile());

        final List<Path> paths = localFsClient.listFiles(testFsPath.toString());
        assertThat(paths.stream().map(p -> p.getFileName().toString()))
                .containsExactlyInAnyOrderElementsOf(List.of("root", "home", "etc"));
        FileUtils.deleteDirectory(testFsPath.toFile());
    }
}