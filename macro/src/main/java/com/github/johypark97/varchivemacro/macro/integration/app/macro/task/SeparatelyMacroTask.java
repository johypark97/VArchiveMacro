package com.github.johypark97.varchivemacro.macro.integration.app.macro.task;

import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.model.MacroDirection;

public class SeparatelyMacroTask extends AbstractMacroTask {
    public SeparatelyMacroTask(MacroConfig config, MacroDirection direction) {
        super(config, direction);
    }

    @Override
    protected void runMacro_forSong() throws InterruptedException {
        capture();
        sleep_postCaptureDelay();

        upload();
    }
}
