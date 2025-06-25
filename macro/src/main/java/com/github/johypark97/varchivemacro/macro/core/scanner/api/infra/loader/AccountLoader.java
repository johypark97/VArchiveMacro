package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.loader;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.Account;

public interface AccountLoader {
    Account load() throws Exception;
}
