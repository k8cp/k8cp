package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.domain.FileSizeContainer;
import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;
import io.github.vcvitaly.k8cp.service.impl.SizeConverterImpl;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class SizeConverterImplTest {

    private final SizeConverterImpl sizeConverter = new SizeConverterImpl();

    @ParameterizedTest
    @MethodSource("params")
    void toFileSizeDtoTest(long size, FileSizeContainer expected) {
        assertThat(sizeConverter.toFileSizeDto(size)).isEqualTo(expected);
    }

    private static Stream<Arguments> params() {
        return Stream.of(
                Arguments.of(1, new FileSizeContainer(1, 1, FileSizeUnit.KB)),
                Arguments.of(1_023, new FileSizeContainer(1023, 1, FileSizeUnit.KB)),
                Arguments.of(1_025, new FileSizeContainer(1025, 1, FileSizeUnit.KB)),
                Arguments.of(2_048, new FileSizeContainer(2_048, 2, FileSizeUnit.KB)),
                Arguments.of(2_000_000, new FileSizeContainer(2_000_000, 1, FileSizeUnit.MB)),
                Arguments.of(2_000_000_000, new FileSizeContainer(2_000_000_000, 1, FileSizeUnit.GB))
        );
    }
}