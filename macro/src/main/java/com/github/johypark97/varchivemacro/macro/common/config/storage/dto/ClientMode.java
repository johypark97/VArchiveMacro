package com.github.johypark97.varchivemacro.macro.common.config.storage.dto;

import com.github.johypark97.varchivemacro.macro.common.config.model.MacroClientMode;

public enum ClientMode {
    AT_ONCE(MacroClientMode.AT_ONCE),
    SEPARATELY(MacroClientMode.SEPARATELY);

    private final MacroClientMode model;

    ClientMode(MacroClientMode model) {
        this.model = model;
    }

    public static ClientMode fromModel(MacroClientMode model) {
        return switch (model) {
            case AT_ONCE -> AT_ONCE;
            case SEPARATELY -> SEPARATELY;
        };
    }

    public MacroClientMode toModel() {
        return model;
    }
}
