package com.github.johypark97.varchivemacro.macro.common.config.domain.model;

import com.google.gson.annotations.Expose;

public record ProgramConfig(@Expose boolean prereleaseNotification) {
    public static final boolean TEST_VERSION_NOTIFICATION_DEFAULT = false;

    public Builder toBuilder() {
        Builder builder = new Builder();

        builder.prereleaseNotification = prereleaseNotification;

        return builder;
    }

    public static class Builder {
        public boolean prereleaseNotification = TEST_VERSION_NOTIFICATION_DEFAULT;

        public ProgramConfig build() {
            return new ProgramConfig(prereleaseNotification);
        }
    }
}
