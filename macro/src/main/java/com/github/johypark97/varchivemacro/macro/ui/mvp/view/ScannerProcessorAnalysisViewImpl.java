package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorAnalysis;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorFrame;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

public class ScannerProcessorAnalysisViewImpl extends VBox
        implements ScannerProcessorAnalysis.View {
    private static final String FXML_PATH = "/fxml/ScannerProcessorAnalysis.fxml";

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label progressLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button functionButton;

    @MvpPresenter
    public ScannerProcessorAnalysis.Presenter presenter;

    public ScannerProcessorAnalysisViewImpl() {
        URL fxmlUrl = ScannerProcessorAnalysisViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setProgress(double value) {
        progressIndicator.setProgress(value);
        progressLabel.setText(String.format("%.2f%%", value * 100));
    }

    @Override
    public void setMessageText(String value) {
        messageLabel.setText(value);
    }

    @Override
    public void setFunctionButton(String text, Runnable onAction) {
        functionButton.setOnAction(event -> onAction.run());
        functionButton.setText(text);
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getLeftButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.analysis.frameButton.back"),
                event -> presenter.showReviewView());
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getRightButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.analysis.frameButton.upload"),
                event -> {
                });
    }
}
