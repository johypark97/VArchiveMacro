package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import javafx.stage.Stage;

public abstract class AbstractCommonStage extends Stage {
    private final Runnable onStopStage;

    public AbstractCommonStage(Runnable onStopStage) {
        this.onStopStage = onStopStage;

        Mvp.hookWindowCloseRequest(this, event -> stopStage());
    }

    public void stopStage() {
        hide();

        if (onStopStage != null) {
            onStopStage.run();
        }
    }
}
