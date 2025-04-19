package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.AnalysisDataRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.NewRecordDataRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.ScanDataRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.database.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.license.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.record.repository.RecordRepository;

public interface RepositoryProvider {
    AnalysisDataRepository getAnalysisDataRepository();

    ConfigRepository getConfigRepository();

    DatabaseRepository getDatabaseRepository();

    NewRecordDataRepository getNewRecordDataRepository();

    OpenSourceLicenseRepository getOpenSourceLicenseRepository();

    RecordRepository getRecordRepository();

    ScanDataRepository getScanDataRepository();
}
