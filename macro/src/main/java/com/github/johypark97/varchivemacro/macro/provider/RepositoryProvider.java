package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;

public interface RepositoryProvider {
    ConfigRepository getConfigRepository();

    DatabaseRepository getDatabaseRepository();

    OpenSourceLicenseRepository getOpenSourceLicenseRepository();

    RecordRepository getRecordRepository();
}
