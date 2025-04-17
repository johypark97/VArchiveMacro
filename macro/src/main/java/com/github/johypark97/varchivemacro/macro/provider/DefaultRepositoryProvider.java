package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.repository.AnalysisDataRepository;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultAnalysisDataRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultDatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultNewRecordDataRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultOpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultRecordRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultScanDataRepository;
import com.github.johypark97.varchivemacro.macro.repository.NewRecordDataRepository;
import com.github.johypark97.varchivemacro.macro.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;
import com.github.johypark97.varchivemacro.macro.repository.ScanDataRepository;

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
