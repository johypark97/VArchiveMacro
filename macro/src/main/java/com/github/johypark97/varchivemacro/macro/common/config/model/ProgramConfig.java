package com.github.johypark97.varchivemacro.macro.common.config.model;

import com.github.johypark97.varchivemacro.macro.common.config.model.value.SettingValue;

public record ProgramConfig(SettingValue<Boolean> prereleaseNotification,
                            SettingValue<Boolean> useSystemProxy,
                            SettingValue<Boolean> useSystemCertificateStore)
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

        @ConfigField
        private SettingValue<Boolean> useSystemProxy = SettingValue.of(false);

        @ConfigField
        private SettingValue<Boolean> useSystemCertificateStore = SettingValue.of(false);

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

        public SettingValue<Boolean> getUseSystemProxy() {
            return useSystemProxy;
        }

        public Editor setUseSystemProxy(boolean value) {
            useSystemProxy = useSystemProxy.with(value);
            return this;
        }

        public SettingValue<Boolean> getUseSystemCertificateStore() {
            return useSystemCertificateStore;
        }

        public Editor setUseSystemCertificateStore(boolean value) {
            useSystemCertificateStore = useSystemCertificateStore.with(value);
            return this;
        }

        @Override
        public ProgramConfig commit() {
            return new ProgramConfig(prereleaseNotification, useSystemProxy,
                    useSystemCertificateStore);
        }
    }
}
