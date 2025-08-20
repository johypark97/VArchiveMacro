package com.github.johypark97.varchivemacro.macro.common.converter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeConverter {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm Z"); // RFC 822

    public static String format(ZonedDateTime zonedDateTime) {
        return formatter.format(zonedDateTime);
    }

    public static ZonedDateTime parse(String string) {
        return ZonedDateTime.parse(string, formatter);
    }
}
