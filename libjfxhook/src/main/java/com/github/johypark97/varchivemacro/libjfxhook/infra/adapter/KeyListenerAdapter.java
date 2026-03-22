package com.github.johypark97.varchivemacro.libjfxhook.infra.adapter;

import com.github.johypark97.varchivemacro.libjfxhook.domain.event.JfxHookKeyListener;
import com.github.johypark97.varchivemacro.libjfxhook.infra.event.DefaultJfxHookKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class KeyListenerAdapter implements NativeKeyListener {
    private final JfxHookKeyListener delegate;

    public KeyListenerAdapter(JfxHookKeyListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        delegate.onKeyPressed(new DefaultJfxHookKeyEvent(nativeEvent));
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        delegate.onKeyReleased(new DefaultJfxHookKeyEvent(nativeEvent));
    }
}
