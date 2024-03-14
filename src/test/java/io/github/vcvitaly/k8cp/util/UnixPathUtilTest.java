package io.github.vcvitaly.k8cp.util;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class UnixPathUtilTest {

    @ParameterizedTest
    @MethodSource("concatPathsParams")
    void concatPathsTest(String path, String file, String expected) {
        assertThat(UnixPathUtil.concatPaths(path, file)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getParentParams")
    void getParentPathTest(String path, String expectedParent) {
        assertThat(UnixPathUtil.getParentPath(path)).isEqualTo(expectedParent);
    }

    @ParameterizedTest
    @MethodSource("getFilenameParams")
    void getFilenameTest(String path, String expectedFilename) {
        assertThat(UnixPathUtil.getFilename(path)).isEqualTo(expectedFilename);
    }

    private static Stream<Arguments> concatPathsParams() {
        return Stream.of(
                Arguments.of("/home", "file.txt", "/home/file.txt"),
                Arguments.of("/home/", "file.txt", "/home/file.txt"),
                Arguments.of("/home", "user", "/home/user"),
                Arguments.of("/home/", "user/", "/home/user")
        );
    }

    private static Stream<Arguments> getParentParams() {
        return Stream.of(
                Arguments.of("/", "/"),
                Arguments.of("/home", "/"),
                Arguments.of("/home/", "/"),
                Arguments.of("/home/user", "/home"),
                Arguments.of("/home/user/", "/home"),
                Arguments.of("/home/user/file.txt", "/home/user")
        );
    }

    private static Stream<Arguments> getFilenameParams() {
        return Stream.of(
                Arguments.of("/", "/"),
                Arguments.of("/home", "home"),
                Arguments.of("/home/", "home"),
                Arguments.of("/home/user", "user"),
                Arguments.of("/home/user/", "user"),
                Arguments.of("/home/user/file.txt", "file.txt")
        );
    }
}