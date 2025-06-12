package com.github.johypark97.varchivemacro.macro.application.utility;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.github.johypark97.varchivemacro.macro.converter.InputKeyConverter;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.InputKeyCombination;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public class NativeInputKey {
    public final InputKey inputKey;
    public final boolean alt;
    public final boolean ctrl;
    public final boolean otherMod;
    public final boolean shift;

    public NativeInputKey(NativeKeyEvent nativeKeyEvent) {
        inputKey = InputKeyConverter.from(nativeKeyEvent);

        int mod = nativeKeyEvent.getModifiers();

        ctrl = (mod & NativeKeyEvent.CTRL_MASK) != 0;
        mod &= ~NativeKeyEvent.CTRL_MASK;

        alt = (mod & NativeKeyEvent.ALT_MASK) != 0;
        mod &= ~NativeKeyEvent.ALT_MASK;

        shift = (mod & NativeKeyEvent.SHIFT_MASK) != 0;
        mod &= ~NativeKeyEvent.SHIFT_MASK;

        otherMod = mod != 0;
    }

    public boolean isInteroperable() {
        return InputKeyConverter.isInteroperable(inputKey);
    }

    public boolean isEqual(InputKeyCombination inputKeyCombination) {
        return inputKeyCombination.key().equals(inputKey) && inputKeyCombination.ctrl() == ctrl
                && inputKeyCombination.alt() == alt && inputKeyCombination.shift() == shift
                && !otherMod;
    }
}
