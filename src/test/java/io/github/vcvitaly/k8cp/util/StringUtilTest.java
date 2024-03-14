package io.github.vcvitaly.k8cp.util;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilTest {

    @ParameterizedTest
    @MethodSource("stripEndingSlashParams")
    void stripEndingSlashTest(String s, String expected) {
        assertThat(StringUtil.stripEndingSlash(s)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("stripBeginningSlashParams")
    void stripBeginningSlashTest(String s, String expected) {
        assertThat(StringUtil.stripBeginningSlash(s)).isEqualTo(expected);
    }

    private static Stream<Arguments> stripEndingSlashParams() {
        return Stream.of(
                Arguments.of("file.txt", "file.txt"),
                Arguments.of("dir/", "dir")
        );
    }

    private static Stream<Arguments> stripBeginningSlashParams() {
        return Stream.of(
                Arguments.of("file.txt", "file.txt"),
                Arguments.of("/dir", "dir")
        );
    }
}