package com.github.johypark97.varchivemacro.macro.common.config.infra.model;

import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.google.gson.annotations.Expose;

public class ConfigJson {
    @Expose
    public MacroConfig macroConfig;

    @Expose
    public ScannerConfig scannerConfig;
}
