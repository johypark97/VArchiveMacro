package com.github.johypark97.varchivemacro.macro.ui.manager;

import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.OpenSourceLicenseStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerCaptureImageViewerStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerScannerStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.SettingStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.UpdateCheckStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.impl.HomeStageImpl;
import com.github.johypark97.varchivemacro.macro.ui.stage.impl.OpenSourceLicenseStageImpl;
import com.github.johypark97.varchivemacro.macro.ui.stage.impl.ScannerCaptureImageViewerStageImpl;
import com.github.johypark97.varchivemacro.macro.ui.stage.impl.ScannerProcessorStageImpl;
import com.github.johypark97.varchivemacro.macro.ui.stage.impl.ScannerScannerStageImpl;
import com.github.johypark97.varchivemacro.macro.ui.stage.impl.ScannerTesterStageImpl;
import com.github.johypark97.varchivemacro.macro.ui.stage.impl.SettingStageImpl;
import com.github.johypark97.varchivemacro.macro.ui.stage.impl.UpdateCheckStageImpl;
import javafx.stage.Stage;

public class StageManager {
    private final Stage primaryStage;

    private HomeStage homeStage;
    private OpenSourceLicenseStage openSourceLicenseStage;
    private ScannerCaptureImageViewerStage captureImageViewerStage;
    private ScannerProcessorStage scannerProcessorStage;
    private ScannerScannerStage scannerScannerStage;
    private SettingStage settingStage;
    private UpdateCheckStage updateCheckStage;

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

    public void showUpdateCheckStage(AbstractTreeableStage parent) {
        if (updateCheckStage != null) {
            updateCheckStage.focusStage();
        }

        updateCheckStage = new UpdateCheckStageImpl(parent, () -> {
            updateCheckStage = null; // NOPMD
        });

        updateCheckStage.startStage();
    }

    public void showSettingStage(AbstractTreeableStage parent) {
        settingStage = new SettingStageImpl(parent, () -> {
            settingStage = null; // NOPMD
        });

        settingStage.startStage();
    }

    public void showScannerScannerStage(AbstractTreeableStage parent) {
        if (scannerProcessorStage != null) {
            scannerProcessorStage.focusStage();
            return;
        }

        if (scannerScannerStage != null) {
            scannerScannerStage.focusStage();
            return;
        }

        scannerScannerStage = new ScannerScannerStageImpl(parent, this, () -> {
            scannerScannerStage = null; // NOPMD
        });

        scannerScannerStage.startStage();
    }

    public void showScannerProcessorStage(AbstractTreeableStage parent,
            ScannerContext scannerContext) {
        if (scannerProcessorStage != null) {
            return;
        }

        scannerProcessorStage = new ScannerProcessorStageImpl(parent, this, scannerContext, () -> {
            scannerProcessorStage = null; // NOPMD
        });

        scannerProcessorStage.startStage();
    }

    public boolean isScannerProcessorStageOpened() {
        return scannerProcessorStage != null;
    }

    public boolean isScannerScannerStageOpened() {
        return scannerScannerStage != null;
    }

    public void showScannerTester(AbstractTreeableStage parent) {
        new ScannerTesterStageImpl(parent).startStage();
    }

    public void showCaptureImageViewer(AbstractTreeableStage parent,
            ScannerContext scannerContext) {
        if (captureImageViewerStage != null) {
            captureImageViewerStage.focusStage();
            return;
        }

        captureImageViewerStage =
                new ScannerCaptureImageViewerStageImpl(parent, scannerContext, () -> {
                    captureImageViewerStage = null; // NOPMD
                });

        captureImageViewerStage.startStage();
    }
}
