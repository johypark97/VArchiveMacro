package com.github.johypark97.varchivemacro.macro.common.config.domain.repository;

import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;

public interface ConfigRepository {
    MacroConfig findMacroConfig();

    void saveMacroConfig(MacroConfig value);

    ScannerConfig findScannerConfig();

    void saveScannerConfig(ScannerConfig value);
}
