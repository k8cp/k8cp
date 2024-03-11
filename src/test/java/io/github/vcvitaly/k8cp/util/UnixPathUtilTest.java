package io.github.vcvitaly.k8cp.util;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class UnixPathUtilTest {

    @ParameterizedTest
    @MethodSource("stripEndingSlashParams")
    void stripEndingSlashFromPathTest(String path, String expected) {
        assertThat(UnixPathUtil.stripEndingSlashFromPath(path)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("concatPathsParams")
    void concatPathsTest(String path, String file, String expected) {
        assertThat(UnixPathUtil.concatPaths(path, file)).isEqualTo(expected);
    }

    private static Stream<Arguments> stripEndingSlashParams() {
        return Stream.of(
                Arguments.of("file.txt", "file.txt"),
                Arguments.of("dir/", "dir")
        );
    }

    private static Stream<Arguments> concatPathsParams() {
        return Stream.of(
                Arguments.of("/home", "file.txt", "/home/file.txt"),
                Arguments.of("/home/", "file.txt", "/home/file.txt"),
                Arguments.of("/home", "user", "/home/user"),
                Arguments.of("/home/", "user/", "/home/user")
        );
    }
}