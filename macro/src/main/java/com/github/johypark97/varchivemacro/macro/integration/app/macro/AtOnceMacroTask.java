package com.github.johypark97.varchivemacro.macro.integration.app.macro;

import com.github.johypark97.varchivemacro.macro.common.config.model.MacroConfig;

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
