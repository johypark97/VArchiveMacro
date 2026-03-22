package com.github.johypark97.varchivemacro.libjfxhook.infra.event;

import com.github.johypark97.varchivemacro.libdesktop.InputKey;
import com.github.johypark97.varchivemacro.libjfxhook.converter.InputKeyConverter;
import com.github.johypark97.varchivemacro.libjfxhook.domain.event.JfxHookKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public class DefaultJfxHookKeyEvent implements JfxHookKeyEvent {
    private static final int OTHER_MOD_MASK =
            ~(NativeKeyEvent.CTRL_MASK | NativeKeyEvent.ALT_MASK | NativeKeyEvent.SHIFT_MASK);

    private final NativeKeyEvent nativeKeyEvent;

    public DefaultJfxHookKeyEvent(NativeKeyEvent nativeKeyEvent) {
        this.nativeKeyEvent = nativeKeyEvent;
    }

    @Override
    public InputKey inputKey() {
        return InputKeyConverter.from(nativeKeyEvent);
    }

    @Override
    public boolean ctrl() {
        return (nativeKeyEvent.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0;
    }

    @Override
    public boolean alt() {
        return (nativeKeyEvent.getModifiers() & NativeKeyEvent.ALT_MASK) != 0;
    }

    @Override
    public boolean shift() {
        return (nativeKeyEvent.getModifiers() & NativeKeyEvent.SHIFT_MASK) != 0;
    }

    @Override
    public boolean otherMod() {
        return (nativeKeyEvent.getModifiers() & OTHER_MOD_MASK) != 0;
    }

    @Override
    public boolean isInteroperable() {
        return InputKeyConverter.isInteroperable(inputKey());
    }
}
