package com.github.johypark97.varchivemacro.macro.infrastructure.config.model;

import com.google.gson.annotations.Expose;

public class AppConfig {
    @Expose
    public MacroConfig macroConfig = new MacroConfig.Builder().build();

    @Expose
    public ScannerConfig scannerConfig = new ScannerConfig.Builder().build();
}
