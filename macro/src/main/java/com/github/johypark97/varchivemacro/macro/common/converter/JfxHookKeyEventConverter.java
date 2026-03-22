package com.github.johypark97.varchivemacro.macro.common.converter;

import com.github.johypark97.varchivemacro.libjfxhook.domain.event.JfxHookKeyEvent;
import com.github.johypark97.varchivemacro.macro.common.config.model.InputKeyCombination;

public class JfxHookKeyEventConverter {
    public static InputKeyCombination toInputKeyCombination(JfxHookKeyEvent event) {
        return new InputKeyCombination(event.inputKey(), event.ctrl(), event.alt(), event.shift());
    }
}
