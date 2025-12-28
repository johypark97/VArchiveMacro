package com.github.johypark97.varchivemacro.macro.common.config.storage.dto;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.github.johypark97.varchivemacro.macro.common.config.model.InputKeyCombination;
import com.google.gson.annotations.Expose;

public class ShortcutKey {
    @Expose
    public InputKey key;

    @Expose
    public boolean ctrl;

    @Expose
    public boolean alt;

    @Expose
    public boolean shift;

    public static ShortcutKey fromModel(InputKeyCombination model) {
        ShortcutKey dto = new ShortcutKey();

        dto.key = model.key();
        dto.ctrl = model.ctrl();
        dto.alt = model.alt();
        dto.shift = model.shift();

        return dto;
    }

    public InputKeyCombination toModel() {
        return new InputKeyCombination(key, ctrl, alt, shift);
    }
}
