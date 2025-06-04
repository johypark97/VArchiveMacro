package com.github.johypark97.varchivemacro.macro.converter;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import javafx.scene.input.KeyCode;

public class InputKeyConverter {
    public static InputKey from(KeyCode keyCode) {
        return InputKey.from(keyCode.getCode());
    }
}
