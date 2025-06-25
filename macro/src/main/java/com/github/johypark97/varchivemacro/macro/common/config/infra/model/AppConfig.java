package com.github.johypark97.varchivemacro.macro.common.config.infra.model;

import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.google.gson.annotations.Expose;

public class AppConfig {
    @Expose
    public MacroConfig macroConfig = new MacroConfig.Builder().build();

    @Expose
    public ScannerConfig scannerConfig = new ScannerConfig.Builder().build();
}
