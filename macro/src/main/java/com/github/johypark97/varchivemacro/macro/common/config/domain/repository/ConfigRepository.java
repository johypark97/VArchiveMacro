package com.github.johypark97.varchivemacro.macro.common.config.domain.repository;

import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import java.io.IOException;

public interface ConfigRepository {
    boolean load() throws IOException;

    void flush() throws IOException;

    MacroConfig findMacroConfig();

    void saveMacroConfig(MacroConfig value);

    ScannerConfig findScannerConfig();

    void saveScannerConfig(ScannerConfig value);
}
