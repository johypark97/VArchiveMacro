package com.github.johypark97.varchivemacro.macro.common.config.app;

import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ProgramConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;

public class ConfigService implements ConfigRepository {
    private static final boolean DEBUG = Boolean.getBoolean("debug");

    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public boolean isDebug() {
        return DEBUG;
    }

    @Override
    public MacroConfig findMacroConfig() {
        return configRepository.findMacroConfig();
    }

    @Override
    public void saveMacroConfig(MacroConfig value) {
        configRepository.saveMacroConfig(value);
    }

    @Override
    public ScannerConfig findScannerConfig() {
        return configRepository.findScannerConfig();
    }

    @Override
    public void saveScannerConfig(ScannerConfig value) {
        configRepository.saveScannerConfig(value);
    }

    @Override
    public ProgramConfig findProgramConfig() {
        return configRepository.findProgramConfig();
    }

    @Override
    public void saveProgramConfig(ProgramConfig value) {
        configRepository.saveProgramConfig(value);
    }
}
