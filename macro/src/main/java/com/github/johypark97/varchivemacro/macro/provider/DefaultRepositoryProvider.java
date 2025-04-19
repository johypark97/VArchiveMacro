package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.AnalysisDataRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.NewRecordDataRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.ScanDataRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.DefaultConfigRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.database.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.database.repository.DefaultDatabaseRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.license.repository.DefaultOpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.license.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.record.repository.DefaultRecordRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.record.repository.RecordRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository.DefaultAnalysisDataRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository.DefaultNewRecordDataRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository.DefaultScanDataRepository;

public class DefaultRepositoryProvider implements RepositoryProvider {
    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    public DefaultRepositoryProvider() {
        // @formatter:off
        instanceManager.setConstructor(AnalysisDataRepository.class, DefaultAnalysisDataRepository::new);
        instanceManager.setConstructor(ConfigRepository.class, DefaultConfigRepository::new);
        instanceManager.setConstructor(DatabaseRepository.class, DefaultDatabaseRepository::new);
        instanceManager.setConstructor(NewRecordDataRepository.class, DefaultNewRecordDataRepository::new);
        instanceManager.setConstructor(OpenSourceLicenseRepository.class, DefaultOpenSourceLicenseRepository::new);
        instanceManager.setConstructor(RecordRepository.class, DefaultRecordRepository::new);
        instanceManager.setConstructor(ScanDataRepository.class, DefaultScanDataRepository::new);
        // @formatter:on
    }

    @Override
    public AnalysisDataRepository getAnalysisDataRepository() {
        return instanceManager.getInstance(AnalysisDataRepository.class);
    }

    @Override
    public ConfigRepository getConfigRepository() {
        return instanceManager.getInstance(ConfigRepository.class);
    }

    @Override
    public DatabaseRepository getDatabaseRepository() {
        return instanceManager.getInstance(DatabaseRepository.class);
    }

    @Override
    public NewRecordDataRepository getNewRecordDataRepository() {
        return instanceManager.getInstance(NewRecordDataRepository.class);
    }

    @Override
    public OpenSourceLicenseRepository getOpenSourceLicenseRepository() {
        return instanceManager.getInstance(OpenSourceLicenseRepository.class);
    }

    @Override
    public RecordRepository getRecordRepository() {
        return instanceManager.getInstance(RecordRepository.class);
    }

    @Override
    public ScanDataRepository getScanDataRepository() {
        return instanceManager.getInstance(ScanDataRepository.class);
    }
}
