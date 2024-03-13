package io.github.vcvitaly.k8cp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtil {

    private static final DateTimeFormatter LONG_ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDateTime toLocalDate(String isoDate, String isoTime) {
        return LocalDateTime.parse("%s %s".formatted(isoDate, isoTime), LONG_ISO_DATE_FORMATTER);
    }

    public static String toString(LocalDateTime localDateTime) {
        return LONG_ISO_DATE_FORMATTER.format(localDateTime);
    }
}
