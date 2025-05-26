package com.github.johypark97.varchivemacro.macro.ui.manager;

import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStageImpl;
import javafx.stage.Stage;

public class StageManager {
    private final Stage primaryStage;

    private HomeStage homeStage;

    public StageManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showHomeStage() {
        if (homeStage != null) {
            return;
        }

        homeStage = new HomeStageImpl(this, primaryStage);
        homeStage.startStage();
    }
}
