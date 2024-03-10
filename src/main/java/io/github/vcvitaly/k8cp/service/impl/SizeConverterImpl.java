package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.dto.FileSizeDto;
import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;
import io.github.vcvitaly.k8cp.service.SizeConverter;
import org.apache.commons.io.FileUtils;

public class SizeConverterImpl implements SizeConverter {
    @Override
    public FileSizeDto toFileSizeDto(long size) {
        if (size < FileUtils.ONE_KB) {
            return new FileSizeDto(size, 1, FileSizeUnit.KB);
        }
        final String s = FileUtils.byteCountToDisplaySize(size);
        final String[] parts = s.split(" ");
        return new FileSizeDto(size, Integer.parseInt(parts[0]), FileSizeUnit.valueOf(parts[1]));
    }
}
