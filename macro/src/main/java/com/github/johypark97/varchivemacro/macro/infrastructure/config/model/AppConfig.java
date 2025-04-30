package com.github.johypark97.varchivemacro.macro.infrastructure.config.model;

import com.google.gson.annotations.Expose;

public class AppConfig {
    @Expose
    private MacroConfig macroConfig = MacroConfig.createDefault();

    @Expose
    private ScannerConfig scannerConfig = ScannerConfig.createDefault();

    public MacroConfig getMacroConfig() {
        return macroConfig;
    }

    public void setMacroConfig(MacroConfig value) {
        this.macroConfig = value;
    }

    public ScannerConfig getScannerConfig() {
        return scannerConfig;
    }

    public void setScannerConfig(ScannerConfig value) {
        this.scannerConfig = value;
    }
}
