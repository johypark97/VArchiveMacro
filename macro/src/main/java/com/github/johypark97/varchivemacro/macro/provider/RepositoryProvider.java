package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.macro.repository.AnalysisDataRepository;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.NewRecordDataRepository;
import com.github.johypark97.varchivemacro.macro.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;
import com.github.johypark97.varchivemacro.macro.repository.ScanDataRepository;

public interface RepositoryProvider {
    AnalysisDataRepository getAnalysisDataRepository();

    ConfigRepository getConfigRepository();

    DatabaseRepository getDatabaseRepository();

    NewRecordDataRepository getNewRecordDataRepository();

    OpenSourceLicenseRepository getOpenSourceLicenseRepository();

    RecordRepository getRecordRepository();

    ScanDataRepository getScanDataRepository();
}
