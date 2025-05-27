package com.github.johypark97.varchivemacro.macro.ui.manager;

import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStageImpl;
import com.github.johypark97.varchivemacro.macro.ui.stage.OpenSourceLicenseStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.OpenSourceLicenseStageImpl;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import javafx.stage.Stage;

public class StageManager {
    private final Stage primaryStage;

    private HomeStage homeStage;
    private OpenSourceLicenseStage openSourceLicenseStage;

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

    public void showOpenSourceLicenseStage(AbstractTreeableStage parent) {
        if (openSourceLicenseStage != null) {
            openSourceLicenseStage.focusStage();
            return;
        }

        openSourceLicenseStage = new OpenSourceLicenseStageImpl(parent, () -> {
            openSourceLicenseStage = null; // NOPMD
        });

        openSourceLicenseStage.startStage();
    }
}
