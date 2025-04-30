package com.github.johypark97.varchivemacro.macro.infrastructure.config.repository;

import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.ScannerConfig;
import java.io.IOException;

public interface ConfigRepository {
    boolean load() throws IOException;

    void flush() throws IOException;

    MacroConfig findMacroConfig();

    void saveMacroConfig(MacroConfig value);

    ScannerConfig findScannerConfig();

    void saveScannerConfig(ScannerConfig value);
}
