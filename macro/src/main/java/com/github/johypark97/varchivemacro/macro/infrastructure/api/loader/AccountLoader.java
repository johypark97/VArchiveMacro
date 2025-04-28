package com.github.johypark97.varchivemacro.macro.infrastructure.api.loader;

import com.github.johypark97.varchivemacro.macro.infrastructure.api.model.Account;

public interface AccountLoader {
    Account load() throws Exception;
}
