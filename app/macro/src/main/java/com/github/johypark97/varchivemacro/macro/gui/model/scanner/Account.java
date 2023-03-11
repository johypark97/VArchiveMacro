package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Account {
    private static final List<Integer> UUID_FIELD_LENGTH = List.of(8, 4, 4, 4, 12);
    private static final String HEX_REGEX = "[0-9A-Fa-f]";
    private static final String FORMAT_ERROR_MESSAGE = "Invalid account file";

    public final UUID token;
    public final int userNo;

    public Account(Path path) throws IOException {
        String firstLine;
        try (Stream<String> stream = Files.lines(path)) {
            firstLine = stream.findFirst().orElseThrow(() -> new IOException(FORMAT_ERROR_MESSAGE));
        }

        String regex = String.format("^(\\d+)\\s(%s)$", getUuidRegex());
        Matcher matcher = Pattern.compile(regex).matcher(firstLine);
        if (!matcher.find()) {
            throw new IOException(FORMAT_ERROR_MESSAGE);
        }

        userNo = Integer.parseInt(matcher.group(1));
        token = UUID.fromString(matcher.group(2));
    }

    public String getUuidRegex() {
        return UUID_FIELD_LENGTH.stream().map((x) -> String.format("%s{%d}", HEX_REGEX, x))
                .collect(Collectors.joining("-"));
    }
}
