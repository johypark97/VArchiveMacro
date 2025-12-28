package com.github.johypark97.varchivemacro.macro.common.config.model.value;

public record SettingValue<T>(T value, T defaultValue) {
    public static <T> SettingValue<T> of(T value) {
        return new SettingValue<>(value, value);
    }

    public SettingValue<T> with(T newValue) {
        return new SettingValue<>(newValue, defaultValue);
    }
}
