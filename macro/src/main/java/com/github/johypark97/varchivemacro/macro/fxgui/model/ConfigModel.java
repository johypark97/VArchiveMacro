package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.google.gson.annotations.Expose;
import java.io.IOException;
import java.util.Set;

public interface ConfigModel {
    boolean load() throws IOException;

    void save() throws IOException;

    ScannerConfig getScannerConfig();

    void setScannerConfig(ScannerConfig value);

    class ScannerConfig {
        @Expose
        public Set<String> selectedTabSet = Set.of();
    }
}
