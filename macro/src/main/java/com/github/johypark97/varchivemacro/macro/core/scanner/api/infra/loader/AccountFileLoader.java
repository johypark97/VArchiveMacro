package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.loader;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.Account;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountFileLoader implements AccountLoader {
    private static final String FORMAT_ERROR_MESSAGE = "Invalid account file";

    private final Path path;

    public AccountFileLoader(Path path) {
        this.path = path;
    }

    protected String createUuidRegex() {
        return Stream.of(8, 4, 4, 4, 12).map((x) -> String.format("[0-9A-Fa-f]{%d}", x))
                .collect(Collectors.joining("-"));
    }

    @Override
    public Account load() throws Exception {
        String firstLine;
        try (Stream<String> stream = Files.lines(path)) {
            firstLine = stream.findFirst().orElseThrow(() -> new IOException(FORMAT_ERROR_MESSAGE));
        }

        String regex = String.format("^(\\d+)\\s(%s)$", createUuidRegex());
        Matcher matcher = Pattern.compile(regex).matcher(firstLine);
        if (!matcher.find()) {
            throw new IOException(FORMAT_ERROR_MESSAGE);
        }

        UUID token = UUID.fromString(matcher.group(2));
        int userNo = Integer.parseInt(matcher.group(1));

        return new Account(token, userNo);
    }
}
