package com.github.johypark97.varchivemacro.libjfxhook.domain.event;

public interface JfxHookKeyListener {
    default void onKeyPressed(JfxHookKeyEvent event) {
    }

    default void onKeyReleased(JfxHookKeyEvent event) {
    }
}
