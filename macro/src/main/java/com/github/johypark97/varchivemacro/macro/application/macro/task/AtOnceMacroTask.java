package com.github.johypark97.varchivemacro.macro.application.macro.task;

import com.github.johypark97.varchivemacro.macro.application.macro.model.MacroDirection;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;

public class AtOnceMacroTask extends AbstractMacroTask {
    public AtOnceMacroTask(MacroConfig config, MacroDirection direction) {
        super(config, direction);
    }

    @Override
    protected void runMacro_forSong() throws InterruptedException {
        upload();
        sleep_postCaptureDelay();
    }
}
