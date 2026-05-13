package com.github.johypark97.varchivemacro.macro.core.scanner.api.account;

import java.nio.file.Path;
import java.util.Objects;

public class AccountFilePathProvider {
    private Path accountFilePath;

    public Path getAccountFilePath() {
        return accountFilePath;
    }

    public void setAccountFilePath(String accountFilePath) {
        this.accountFilePath = Path.of(Objects.requireNonNull(accountFilePath));
    }
}
