package com.github.johypark97.varchivemacro.macro.core.scanner.api.account;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.exception.InvalidAccountFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountFileReader {
    private final AccountFilePathProvider accountFilePathProvider;

    public AccountFileReader(AccountFilePathProvider accountFilePathProvider) {
        this.accountFilePathProvider = Objects.requireNonNull(accountFilePathProvider);
    }

    public Account read() throws IOException, InvalidAccountFileException {
        String firstLine;
        try (Stream<String> stream = Files.lines(accountFilePathProvider.getAccountFilePath())) {
            firstLine = stream.findFirst().orElseThrow(InvalidAccountFileException::new);
        }

        String regex = "^(\\d+)\\s(%s)$".formatted(createUuidRegex());
        Matcher matcher = Pattern.compile(regex).matcher(firstLine);
        if (!matcher.find()) {
            throw new InvalidAccountFileException();
        }

        UUID token = UUID.fromString(matcher.group(2));
        int userNo = Integer.parseInt(matcher.group(1));

        return new Account(token, userNo);
    }

    protected String createUuidRegex() {
        return Stream.of(8, 4, 4, 4, 12)
                .map("[0-9A-Fa-f]{%d}"::formatted)
                .collect(Collectors.joining("-"));
    }
}
