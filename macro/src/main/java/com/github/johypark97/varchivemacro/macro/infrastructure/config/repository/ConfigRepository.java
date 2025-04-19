package com.github.johypark97.varchivemacro.macro.infrastructure.config.repository;

import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.ScannerConfig;
import java.io.IOException;

public interface ConfigRepository {
    boolean load() throws IOException;

    void save() throws IOException;

    ScannerConfig getScannerConfig();

    MacroConfig getMacroConfig();

    void setScannerConfig(ScannerConfig value);

    void setMacroConfig(MacroConfig value);
}
