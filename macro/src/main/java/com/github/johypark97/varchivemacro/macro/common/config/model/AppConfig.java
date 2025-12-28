package com.github.johypark97.varchivemacro.macro.common.config.model;

import java.util.function.UnaryOperator;

public record AppConfig(MacroConfig macroConfig,
                        ScannerConfig scannerConfig,
                        ProgramConfig programConfig)
        implements ConfigEditorModel.Config<AppConfig, AppConfig.Editor> {
    private static final boolean DEBUG = Boolean.getBoolean("debug");

    public static AppConfig ofDefault() {
        return editDefault().commit();
    }

    public static AppConfig.Editor editDefault() {
        return new AppConfig.Editor();
    }

    public boolean debug() {
        return DEBUG;
    }

    @Override
    public Editor edit() {
        return new Editor(this);
    }

    public static final class Editor extends ConfigEditorModel.Editor<AppConfig, Editor> {
        @ConfigField
        private MacroConfig macroConfig = MacroConfig.ofDefault();

        @ConfigField
        private ScannerConfig scannerConfig = ScannerConfig.ofDefault();

        @ConfigField
        private ProgramConfig programConfig = ProgramConfig.ofDefault();

        private Editor() {
        }

        private Editor(AppConfig config) {
            copyFrom(config);
        }

        public MacroConfig getMacroConfig() {
            return macroConfig;
        }

        public Editor editMacroConfig(UnaryOperator<MacroConfig.Editor> editFunction) {
            macroConfig = editFunction.apply(macroConfig.edit()).commit();
            return this;
        }

        public ScannerConfig getScannerConfig() {
            return scannerConfig;
        }

        public Editor editScannerConfig(UnaryOperator<ScannerConfig.Editor> editFunction) {
            scannerConfig = editFunction.apply(scannerConfig.edit()).commit();
            return this;
        }

        public ProgramConfig getProgramConfig() {
            return programConfig;
        }

        public Editor editProgramConfig(UnaryOperator<ProgramConfig.Editor> editFunction) {
            programConfig = editFunction.apply(programConfig.edit()).commit();
            return this;
        }

        @Override
        public AppConfig commit() {
            return new AppConfig(macroConfig, scannerConfig, programConfig);
        }
    }
}
