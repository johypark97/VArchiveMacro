package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.common.SimpleTransition;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorFrame;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ScannerProcessorFrameViewImpl extends BorderPane
        implements ScannerProcessorFrame.View {
    private static final String FXML_PATH = "/fxml/ScannerProcessorFrame.fxml";

    private static final Duration HEADER_BOX_EFFECT_DURATION = Duration.millis(500);
    private static final int HEADER_LABEL_EFFECT_REPEAT = 6;

    @FXML
    private Label reviewLabel;

    @FXML
    private Label analysisLabel;

    @FXML
    private Label uploadLabel;

    @FXML
    private HBox headerBox;

    @FXML
    private Label headerLabel;

    @FXML
    private Button captureImageViewerButton;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;

    @MvpPresenter
    public ScannerProcessorFrame.Presenter presenter;

    private SimpleTransition headerBoxTransition;

    public ScannerProcessorFrameViewImpl() {
        URL fxmlUrl = ScannerProcessorFrameViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        CornerRadii headerBoxCornerRadii = new CornerRadii(10);
        headerBoxTransition = new SimpleTransition(HEADER_BOX_EFFECT_DURATION,
                x -> headerBox.setBackground(new Background(
                        new BackgroundFill(new Color(0, 1, 0, x / 2), headerBoxCornerRadii,
                                Insets.EMPTY))));
        headerBoxTransition.setAutoReverse(true);
        headerBoxTransition.setCycleCount(HEADER_LABEL_EFFECT_REPEAT);

        headerLabel.setText(null);

        captureImageViewerButton.setOnAction(event -> presenter.showCaptureImageViewer());
    }

    @Override
    public void setHeaderMessage(String text) {
        headerBoxTransition.playFromStart();
        headerLabel.setText(text);
    }

    @Override
    public void setCenterNode(Node value) {
        setCenter(value);
    }

    @Override
    public void setLeftButtonFunction(ScannerProcessorFrame.ButtonFunction value) {
        leftButton.setOnAction(value.eventHandler());
        leftButton.setText(value.text());
    }

    @Override
    public void setRightButtonFunction(ScannerProcessorFrame.ButtonFunction value) {
        rightButton.setOnAction(value.eventHandler());
        rightButton.setText(value.text());
    }
}
