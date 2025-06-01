package com.github.johypark97.varchivemacro.macro.infrastructure.config.model;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import java.util.ArrayList;
import java.util.List;

public record InputKeyCombination(InputKey key, boolean ctrl, boolean alt, boolean shift) {
    public static InputKeyCombination from(InputKey key) {
        return new InputKeyCombination(key, false, false, false);
    }

    public List<InputKey> modifierList() {
        List<InputKey> list = new ArrayList<>(3);

        if (ctrl) {
            list.add(InputKey.CONTROL);
        }

        if (alt) {
            list.add(InputKey.ALT);
        }

        if (shift) {
            list.add(InputKey.SHIFT);
        }

        return list;
    }

    public int[] modifierKeyCodeArray() {
        return modifierList().stream().mapToInt(InputKey::toAwtKeyCode).toArray();
    }
}
