package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Setting;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

public class SettingViewImpl extends VBox implements Setting.SettingView {
    private static final String FXML_PATH = "/fxml/Setting.fxml";

    @FXML
    private RadioButton macroClientModeSeparatelyRadioButton;

    @FXML
    private RadioButton macroClientModeAtOnceRadioButton;

    @FXML
    private TextField macroClientUploadKeyTextField;

    @FXML
    private TextField macroStartUpKeyTextField;

    @FXML
    private TextField macroStartDownKeyTextField;

    @FXML
    private TextField macroStopKeyTextField;

    @FXML
    private Slider macroSongSwitchingTimeSlider;

    @FXML
    private TextField macroSongSwitchingTimeTextField;

    @FXML
    private Slider macroPostCaptureDelaySlider;

    @FXML
    private TextField macroPostCaptureDelayTextField;

    @FXML
    private Slider macroKeyHoldTimeSlider;

    @FXML
    private TextField macroKeyHoldTimeTextField;

    @FXML
    private TextField scannerAccountFileTextField;

    @FXML
    private Button scannerAccountFileSelectButton;

    @FXML
    private TextField scannerStartKeyTextField;

    @FXML
    private TextField scannerStopKeyTextField;

    @FXML
    private TextField scannerCacheDirectoryTextField;

    @FXML
    private Button scannerCacheDirectorySelectButton;

    @FXML
    private ToggleButton scannerAutoAnalysisToggleButton;

    @FXML
    private Slider scannerAnalyzerThreadCountSlider;

    @FXML
    private TextField scannerAnalyzerThreadCountTextField;

    @FXML
    private Slider scannerCaptureDelaySlider;

    @FXML
    private TextField scannerCaptureDelayTextField;

    @FXML
    private Slider scannerKeyHoldTimeSlider;

    @FXML
    private TextField scannerKeyHoldTimeTextField;

    @FXML
    private Button applyButton;

    @FXML
    private Button revertButton;

    @FXML
    private Button closeButton;

    @MvpPresenter
    public Setting.SettingPresenter presenter;

    public SettingViewImpl() {
        URL fxmlUrl = SettingViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
    }
}
