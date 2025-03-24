package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultDatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultOpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.repository.DefaultRecordRepository;
import com.github.johypark97.varchivemacro.macro.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;

public class DefaultRepositoryProvider implements RepositoryProvider {
    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    public DefaultRepositoryProvider() {
        // @formatter:off
        instanceManager.setConstructor(ConfigRepository.class, DefaultConfigRepository::new);
        instanceManager.setConstructor(DatabaseRepository.class, DefaultDatabaseRepository::new);
        instanceManager.setConstructor(OpenSourceLicenseRepository.class, DefaultOpenSourceLicenseRepository::new);
        instanceManager.setConstructor(RecordRepository.class, DefaultRecordRepository::new);
        // @formatter:on
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
    public OpenSourceLicenseRepository getOpenSourceLicenseRepository() {
        return instanceManager.getInstance(OpenSourceLicenseRepository.class);
    }

    @Override
    public RecordRepository getRecordRepository() {
        return instanceManager.getInstance(RecordRepository.class);
    }
}
