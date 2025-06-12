package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Macro;
import com.github.johypark97.varchivemacro.macro.ui.utility.SimpleTransition;
import java.io.IOException;
import java.net.URL;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MacroViewImpl extends BorderPane implements Macro.MacroView {
    private static final String FXML_PATH = "/fxml/Macro.fxml";

    private static final Duration PROGRESS_TRANSITION_DURATION = Duration.millis(500);
    private static final int PROGRESS_TRANSITION_BLUR_RADIUS = 8;

    private final GaussianBlur macroBoxBlur = new GaussianBlur(0);

    @FXML
    private VBox macroBox;

    @FXML
    private Button countDecrease10Button;

    @FXML
    private Button countDecrease1Button;

    @FXML
    private TextField countTextField;

    @FXML
    private Button countIncrease1Button;

    @FXML
    private Button countIncrease10Button;

    @FXML
    private Slider countSlider;

    @FXML
    private Label clientModeLabel;

    @FXML
    private Label uploadKeyLabel;

    @FXML
    private Label startUpKeyLabel;

    @FXML
    private Label startDownKeyLabel;

    @FXML
    private Label stopKeyLabel;

    @FXML
    private VBox progressBox;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Button homeButton;

    @MvpPresenter
    public Macro.MacroPresenter presenter;

    private SliderTextFieldLinker countLinker;

    public MacroViewImpl() {
        URL fxmlUrl = MacroViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        macroBox.setEffect(macroBoxBlur);

        countLinker = new SliderTextFieldLinker(countSlider, countTextField);
        countLinker.valueProperty().addListener(
                (observable, oldValue, newValue) -> presenter.updateCount(newValue.intValue()));

        countDecrease10Button.setOnAction(event -> presenter.decreaseCount10());
        countDecrease1Button.setOnAction(event -> presenter.decreaseCount1());
        countIncrease1Button.setOnAction(event -> presenter.increaseCount1());
        countIncrease10Button.setOnAction(event -> presenter.increaseCount10());

        progressBox.setVisible(false);

        homeButton.setOnAction(event -> presenter.showHome());

        Platform.runLater(this::requestFocus);
    }

    @Override
    public void setupCountSlider(int value, int defaultValue, int min, int max) {
        countLinker.setDefaultValue(defaultValue);
        countLinker.setLimitMax(max);
        countLinker.setLimitMin(min);
        countLinker.setValue(value);
    }

    @Override
    public void setCount(int value) {
        countLinker.setValue(value);
    }

    @Override
    public void setClientModeText(String value) {
        clientModeLabel.setText(value);
    }

    @Override
    public void setUploadKeyText(String value) {
        uploadKeyLabel.setText(value);
    }

    @Override
    public void setStartUpKeyText(String value) {
        startUpKeyLabel.setText(value);
    }

    @Override
    public void setStartDownKeyText(String value) {
        startDownKeyLabel.setText(value);
    }

    @Override
    public void setStopKeyText(String value) {
        stopKeyLabel.setText(value);
    }

    @Override
    public void showProgressBox() {
        macroBox.setDisable(true);

        progressBox.setVisible(true);

        SimpleTransition blurTransition = new SimpleTransition(PROGRESS_TRANSITION_DURATION,
                x -> macroBoxBlur.setRadius(x * PROGRESS_TRANSITION_BLUR_RADIUS));
        SimpleTransition fadeTransition =
                new SimpleTransition(PROGRESS_TRANSITION_DURATION, progressBox::setOpacity);

        new ParallelTransition(blurTransition, fadeTransition).play();
    }

    @Override
    public void hideProgressBox() {
        macroBox.setDisable(false);

        SimpleTransition blurTransition = new SimpleTransition(PROGRESS_TRANSITION_DURATION,
                x -> macroBoxBlur.setRadius(PROGRESS_TRANSITION_BLUR_RADIUS * (1 - x)));
        SimpleTransition fadeTransition = new SimpleTransition(PROGRESS_TRANSITION_DURATION,
                x -> progressBox.setOpacity(1 - x));
        fadeTransition.setOnFinished(event -> progressBox.setVisible(false));

        new ParallelTransition(blurTransition, fadeTransition).play();
    }

    @Override
    public void setProgress(int value, int max) {
        double x = (double) value / max;

        progressBar.setProgress(x);
        progressLabel.setText(String.format("%.02f%% (%d / %d)", x * 100, value, max));
    }
}
