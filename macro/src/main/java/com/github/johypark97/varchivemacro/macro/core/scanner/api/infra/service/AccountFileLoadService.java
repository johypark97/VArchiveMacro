package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.service;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.exception.InvalidAccountFileException;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.Account;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountFileLoadService {
    private final Path accountFilePath;

    public AccountFileLoadService(Path accountFilePath) {
        this.accountFilePath = accountFilePath;
    }

    public static void validate(Path accountFilePath)
            throws IOException, InvalidAccountFileException {
        new AccountFileLoadService(accountFilePath).readAccountFile();
    }

    public Account load() throws IOException, InvalidAccountFileException {
        Matcher matcher = readAccountFile();

        UUID token = UUID.fromString(matcher.group(2));
        int userNo = Integer.parseInt(matcher.group(1));

        return new Account(token, userNo);
    }

    protected Matcher readAccountFile() throws IOException, InvalidAccountFileException {
        String firstLine;
        try (Stream<String> stream = Files.lines(accountFilePath)) {
            firstLine = stream.findFirst().orElse("");
        }

        String regex = String.format("^(\\d+)\\s(%s)$", createUuidRegex());
        Matcher matcher = Pattern.compile(regex).matcher(firstLine);
        if (!matcher.find()) {
            throw new InvalidAccountFileException();
        }

        return matcher;
    }

    protected String createUuidRegex() {
        return Stream.of(8, 4, 4, 4, 12).map((x) -> String.format("[0-9A-Fa-f]{%d}", x))
                .collect(Collectors.joining("-"));
    }
}
