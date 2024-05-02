package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel.AnalysisKey;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class MacroComponent extends BorderPane {
    private static final String FXML_FILE_NAME = "Macro.fxml";

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

    public MacroComponent() {
        URL url = MacroComponent.class.getResource(FXML_FILE_NAME);
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

    public void setAnalysisKey(AnalysisKey key) {
        RadioButton button = switch (key) {
            case F11 -> analysisKeyRadioButton_f11;
            case F12 -> analysisKeyRadioButton_f12;
            case HOME -> analysisKeyRadioButton_home;
            case INSERT -> analysisKeyRadioButton_insert;
        };

        button.setSelected(true);
    }
}
