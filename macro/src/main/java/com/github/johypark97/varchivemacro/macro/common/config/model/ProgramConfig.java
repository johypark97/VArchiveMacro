package com.github.johypark97.varchivemacro.macro.common.config.model;

import com.github.johypark97.varchivemacro.macro.common.config.model.value.SettingValue;

public record ProgramConfig(SettingValue<Boolean> prereleaseNotification)
        implements ConfigEditorModel.Config<ProgramConfig, ProgramConfig.Editor> {
    public static ProgramConfig ofDefault() {
        return editDefault().commit();
    }

    public static ProgramConfig.Editor editDefault() {
        return new ProgramConfig.Editor();
    }

    @Override
    public Editor edit() {
        return new Editor(this);
    }

    public static final class Editor extends ConfigEditorModel.Editor<ProgramConfig, Editor> {
        @ConfigField
        private SettingValue<Boolean> prereleaseNotification = SettingValue.of(false);

        private Editor() {
        }

        private Editor(ProgramConfig config) {
            copyFrom(config);
        }

        public SettingValue<Boolean> getPrereleaseNotification() {
            return prereleaseNotification;
        }

        public Editor setPrereleaseNotification(boolean value) {
            prereleaseNotification = prereleaseNotification.with(value);
            return this;
        }

        @Override
        public ProgramConfig commit() {
            return new ProgramConfig(prereleaseNotification);
        }
    }
}
