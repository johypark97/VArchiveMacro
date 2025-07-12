package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.desktop.AwtRobotHelper;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.exception.DisplayResolutionException;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerTesterStage;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ScannerTesterPresenterImpl implements ScannerTester.Presenter {
    private final ScannerTesterStage scannerTesterStage;

    private final GlobalContext globalContext;

    @MvpView
    public ScannerTester.View view;

    public ScannerTesterPresenterImpl(ScannerTesterStage scannerTesterStage,
            GlobalContext globalContext) {
        this.scannerTesterStage = scannerTesterStage;

        this.globalContext = globalContext;
    }

    @Override
    public void startView() {
        // TODO: Refactoring required.

        Language language = Language.INSTANCE;

        BufferedImage bufferedImage;
        try {
            bufferedImage = AwtRobotHelper.captureScreenshot(new Robot());
            Dimension dimension =
                    new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
            globalContext.captureRegionService.create(dimension);
        } catch (DisplayResolutionException e) {
            scannerTesterStage.showWarning(language.getFormatString(
                    "scanner.tester.dialog.exception.notSupportedResolution", e.getMessage()));
            return;
        } catch (Exception e) {
            String message = "Unexpected exception";
            scannerTesterStage.showError(message, e);
            return;
        }

        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        view.setImage(image);

        scannerTesterStage.showInformation(
                language.getFormatString("scanner.tester.dialog.testHeader"),
                language.getFormatString("scanner.tester.dialog.testMessage"));
    }

    @Override
    public void requestStopStage() {
        scannerTesterStage.stopStage();
    }
}
