package com.github.johypark97.varchivemacro.macro.common.config.model.value;

public record RangedSettingValue<T>(T value, T defaultValue, T min, T max) {
    public static <T> RangedSettingValue<T> of(T value, T min, T max) {
        return new RangedSettingValue<>(value, value, min, max);
    }

    public RangedSettingValue<T> with(T newValue) {
        return new RangedSettingValue<>(newValue, defaultValue, min, max);
    }
}
