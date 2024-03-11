package com.github.johypark97.varchivemacro.lib.common;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public class NativeKeyEventData {
    private final boolean alt;
    private final boolean ctrl;
    private final boolean otherMod;
    private final boolean shift;
    private final int keyCode;

    public NativeKeyEventData(NativeKeyEvent nativeEvent) {
        keyCode = nativeEvent.getKeyCode();

        int mod = nativeEvent.getModifiers();

        ctrl = (mod & NativeKeyEvent.CTRL_MASK) != 0;
        mod &= ~NativeKeyEvent.CTRL_MASK;

        alt = (mod & NativeKeyEvent.ALT_MASK) != 0;
        mod &= ~NativeKeyEvent.ALT_MASK;

        shift = (mod & NativeKeyEvent.SHIFT_MASK) != 0;
        mod &= ~NativeKeyEvent.SHIFT_MASK;

        otherMod = mod != 0;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public boolean isCtrl() {
        return ctrl;
    }

    public boolean isAlt() {
        return alt;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isOtherMod() {
        return otherMod;
    }

    public boolean isPressed(int keyCode) {
        return this.keyCode == keyCode;
    }
}
