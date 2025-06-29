package com.github.johypark97.varchivemacro.macro.common.validator;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.exception.InvalidAccountFileException;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.service.AccountFileLoadService;
import java.io.IOException;
import java.nio.file.Path;

public class AccountFileValidator {
    public static void validate(Path accountFilePath)
            throws IOException, InvalidAccountFileException {
        AccountFileLoadService.validate(accountFilePath);
    }
}
