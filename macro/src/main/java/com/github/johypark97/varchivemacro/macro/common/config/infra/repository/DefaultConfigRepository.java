package com.github.johypark97.varchivemacro.macro.common.config.infra.repository;

import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ProgramConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;

public class DefaultConfigRepository implements ConfigRepository {
    private MacroConfig macroConfig = new MacroConfig.Builder().build();
    private ScannerConfig scannerConfig = new ScannerConfig.Builder().build();
    private ProgramConfig programConfig = new ProgramConfig.Builder().build();

    @Override
    public MacroConfig findMacroConfig() {
        return macroConfig;
    }

    @Override
    public void saveMacroConfig(MacroConfig value) {
        macroConfig = value;
    }

    @Override
    public ScannerConfig findScannerConfig() {
        return scannerConfig;
    }

    @Override
    public void saveScannerConfig(ScannerConfig value) {
        scannerConfig = value;
    }

    @Override
    public ProgramConfig findProgramConfig() {
        return programConfig;
    }

    @Override
    public void saveProgramConfig(ProgramConfig value) {
        programConfig = value;
    }
}
