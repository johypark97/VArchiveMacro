package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro;

import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro.Macro.MacroPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro.Macro.MacroView;
import com.github.johypark97.varchivemacro.macro.model.AnalysisKey;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class MacroViewImpl extends BorderPane implements MacroView {
    private static final String FXML_PATH = "/fxml/Macro.fxml";

    @MvpPresenter
    public MacroPresenter presenter;

    @FXML
    public RadioButton analysisKeyRadioButton_f11;

    @FXML
    public RadioButton analysisKeyRadioButton_f12;

    @FXML
    public RadioButton analysisKeyRadioButton_home;

    @FXML
    public RadioButton analysisKeyRadioButton_insert;

    @FXML
    public Slider countSlider;

    @FXML
    public TextField countTextField;

    @FXML
    public Slider captureDelaySlider;

    @FXML
    public TextField captureDelayTextField;

    @FXML
    public Slider captureDurationSlider;

    @FXML
    public TextField captureDurationTextField;

    @FXML
    public Slider keyInputDurationSlider;

    @FXML
    public TextField keyInputDurationTextField;

    public SliderTextFieldLinker countLinker;
    public SliderTextFieldLinker captureDelayLinker;
    public SliderTextFieldLinker captureDurationLinker;
    public SliderTextFieldLinker keyInputDurationLinker;

    public MacroViewImpl() {
        URL url = MacroViewImpl.class.getResource(FXML_PATH);
        MvpFxml.loadRoot(this, url, Language.getInstance().getResourceBundle());
    }

    @FXML
    public void initialize() {
        countLinker = new SliderTextFieldLinker(countSlider, countTextField);

        captureDelayLinker = new SliderTextFieldLinker(captureDelaySlider, captureDelayTextField);

        captureDurationLinker =
                new SliderTextFieldLinker(captureDurationSlider, captureDurationTextField);

        keyInputDurationLinker =
                new SliderTextFieldLinker(keyInputDurationSlider, keyInputDurationTextField);
    }

    @Override
    public void startView() {
        presenter.onStartView();
    }

    @Override
    public void stopView() {
        presenter.onStopView();
    }

    @Override
    public AnalysisKey getAnalysisKey() {
        if (analysisKeyRadioButton_f11.isSelected()) {
            return AnalysisKey.F11;
        } else if (analysisKeyRadioButton_f12.isSelected()) {
            return AnalysisKey.F12;
        } else if (analysisKeyRadioButton_home.isSelected()) {
            return AnalysisKey.HOME;
        } else if (analysisKeyRadioButton_insert.isSelected()) {
            return AnalysisKey.INSERT;
        }

        setAnalysisKey(AnalysisKey.F11);
        return AnalysisKey.F11;
    }

    @Override
    public void setAnalysisKey(AnalysisKey key) {
        RadioButton button = switch (key) {
            case F11 -> analysisKeyRadioButton_f11;
            case F12 -> analysisKeyRadioButton_f12;
            case HOME -> analysisKeyRadioButton_home;
            case INSERT -> analysisKeyRadioButton_insert;
        };

        button.setSelected(true);
    }

    @Override
    public void setupCountSlider(int defaultValue, int limitMax, int limitMin, int value) {
        countLinker.setDefaultValue(defaultValue);
        countLinker.setLimitMax(limitMax);
        countLinker.setLimitMin(limitMin);
        countLinker.setValue(value);
    }

    @Override
    public int getCount() {
        return countLinker.getValue();
    }

    @Override
    public void setupCaptureDelaySlider(int defaultValue, int limitMax, int limitMin, int value) {
        captureDelayLinker.setDefaultValue(defaultValue);
        captureDelayLinker.setLimitMax(limitMax);
        captureDelayLinker.setLimitMin(limitMin);
        captureDelayLinker.setValue(value);
    }

    @Override
    public int getCaptureDelay() {
        return captureDelayLinker.getValue();
    }

    @Override
    public void setupCaptureDurationSlider(int defaultValue, int limitMax, int limitMin,
            int value) {
        captureDurationLinker.setDefaultValue(defaultValue);
        captureDurationLinker.setLimitMax(limitMax);
        captureDurationLinker.setLimitMin(limitMin);
        captureDurationLinker.setValue(value);
    }

    @Override
    public int getCaptureDuration() {
        return captureDurationLinker.getValue();
    }

    @Override
    public void setupKeyInputDurationSlider(int defaultValue, int limitMax, int limitMin,
            int value) {
        keyInputDurationLinker.setDefaultValue(defaultValue);
        keyInputDurationLinker.setLimitMax(limitMax);
        keyInputDurationLinker.setLimitMin(limitMin);
        keyInputDurationLinker.setValue(value);
    }

    @Override
    public int getKeyInputDuration() {
        return keyInputDurationLinker.getValue();
    }
}
