package com.github.johypark97.varchivemacro.libjfxhook.domain.event;

import com.github.johypark97.varchivemacro.libdesktop.InputKey;

public interface JfxHookKeyEvent {
    InputKey inputKey();

    boolean ctrl();

    boolean alt();

    boolean shift();

    boolean otherMod();

    boolean isInteroperable();
}
