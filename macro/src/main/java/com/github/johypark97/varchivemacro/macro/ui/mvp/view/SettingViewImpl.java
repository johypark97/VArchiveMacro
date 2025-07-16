package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroClientMode;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.mvp.Setting;
import java.io.IOException;
import java.net.URL;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class SettingViewImpl extends VBox implements Setting.View {
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

    private SliderTextFieldLinker macroKeyHoldTimeLinker;
    private SliderTextFieldLinker macroPostCaptureDelayLinker;
    private SliderTextFieldLinker macroSongSwitchingTimeLinker;
    private SliderTextFieldLinker scannerAnalyzerThreadCountLinker;
    private SliderTextFieldLinker scannerCaptureDelayLinker;
    private SliderTextFieldLinker scannerKeyHoldTimeLinker;

    @MvpPresenter
    public Setting.Presenter presenter;

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
        setupMacro();
        setupScanner();
        setupButtonBar();
    }

    private void setupMacro() {
        // client
        macroClientModeAtOnceRadioButton.setOnAction(
                event -> presenter.macro_onChangeClientMode(MacroClientMode.AT_ONCE));
        macroClientModeSeparatelyRadioButton.setOnAction(
                event -> presenter.macro_onChangeClientMode(MacroClientMode.SEPARATELY));

        macroClientUploadKeyTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            event.consume();
            presenter.macro_onChangeUploadKey(event);
        });

        // shortcut
        macroStartUpKeyTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            event.consume();
            presenter.macro_onChangeStartUpKey(event);
        });

        macroStartDownKeyTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            event.consume();
            presenter.macro_onChangeStartDownKey(event);
        });

        macroStopKeyTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            event.consume();
            presenter.macro_onChangeStopKey(event);
        });

        // advanced
        macroSongSwitchingTimeLinker = new SliderTextFieldLinker(macroSongSwitchingTimeSlider,
                macroSongSwitchingTimeTextField);
        macroSongSwitchingTimeLinker.valueProperty().addListener(
                (observable, oldValue, newValue) -> presenter.macro_onChangeSongSwitchingTime(
                        newValue.intValue()));

        macroPostCaptureDelayLinker = new SliderTextFieldLinker(macroPostCaptureDelaySlider,
                macroPostCaptureDelayTextField);
        macroPostCaptureDelayLinker.valueProperty().addListener(
                (observable, oldValue, newValue) -> presenter.macro_onChangePostCaptureDelay(
                        newValue.intValue()));

        macroKeyHoldTimeLinker =
                new SliderTextFieldLinker(macroKeyHoldTimeSlider, macroKeyHoldTimeTextField);
        macroKeyHoldTimeLinker.valueProperty().addListener(
                (observable, oldValue, newValue) -> presenter.macro_onChangeKeyHoldTime(
                        newValue.intValue()));
    }

    private void setupScanner() {
        // account
        scannerAccountFileTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> presenter.scanner_onChangeAccountFile(
                        newValue));

        scannerAccountFileSelectButton.setOnAction(
                event -> presenter.scanner_showAccountFileSelector());

        // shortcut
        scannerStartKeyTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            event.consume();
            presenter.scanner_onChangeStartKey(event);
        });

        scannerStopKeyTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            event.consume();
            presenter.scanner_onChangeStopKey(event);
        });

        // cache
        scannerCacheDirectoryTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> presenter.scanner_onChangeCacheDirectory(
                        newValue));

        scannerCacheDirectorySelectButton.setOnAction(
                event -> presenter.scanner_showCacheDirectorySelector());

        // analyzer
        scannerAutoAnalysisToggleButton.setOnAction(event -> presenter.scanner_onChangeAutoAnalysis(
                scannerAutoAnalysisToggleButton.isSelected()));
        scannerAutoAnalysisToggleButton.textProperty()
                .bind(Bindings.when(scannerAutoAnalysisToggleButton.selectedProperty()).then("ON")
                        .otherwise("OFF"));

        // advanced
        scannerAnalyzerThreadCountLinker =
                new SliderTextFieldLinker(scannerAnalyzerThreadCountSlider,
                        scannerAnalyzerThreadCountTextField);
        scannerAnalyzerThreadCountLinker.valueProperty().addListener(
                (observable, oldValue, newValue) -> presenter.scanner_onChangeAnalyzerThreadCount(
                        newValue.intValue()));

        scannerCaptureDelayLinker =
                new SliderTextFieldLinker(scannerCaptureDelaySlider, scannerCaptureDelayTextField);
        scannerCaptureDelayLinker.valueProperty().addListener(
                (observable, oldValue, newValue) -> presenter.scanner_onChangeCaptureDelay(
                        newValue.intValue()));

        scannerKeyHoldTimeLinker =
                new SliderTextFieldLinker(scannerKeyHoldTimeSlider, scannerKeyHoldTimeTextField);
        scannerKeyHoldTimeLinker.valueProperty().addListener(
                (observable, oldValue, newValue) -> presenter.scanner_onChangeKeyHoldTime(
                        newValue.intValue()));
    }

    private void setupButtonBar() {
        applyButton.setOnAction(event -> presenter.apply());
        revertButton.setOnAction(event -> presenter.revert());
        closeButton.setOnAction(event -> presenter.close());
    }

    @Override
    public void bindApplyButtonEnable(ObservableBooleanValue value) {
        applyButton.disableProperty().bind(Bindings.not(value));
    }

    @Override
    public void bindRevertButtonEnable(ObservableBooleanValue value) {
        revertButton.disableProperty().bind(Bindings.not(value));
    }

    @Override
    public void setMacroClientMode(MacroClientMode value) {
        switch (value) {
            case AT_ONCE -> macroClientModeAtOnceRadioButton.setSelected(true);
            case SEPARATELY -> macroClientModeSeparatelyRadioButton.setSelected(true);
        }
    }

    @Override
    public void setMacroUploadKeyText(String value) {
        macroClientUploadKeyTextField.setText(value);
    }

    @Override
    public void setMacroStartUpKeyText(String value) {
        macroStartUpKeyTextField.setText(value);
    }

    @Override
    public void setMacroStartDownKeyText(String value) {
        macroStartDownKeyTextField.setText(value);
    }

    @Override
    public void setMacroStopKeyText(String value) {
        macroStopKeyTextField.setText(value);
    }

    @Override
    public void setupMacroSongSwitchingTimeSlider(int value, int defaultValue, int min, int max) {
        macroSongSwitchingTimeLinker.setDefaultValue(defaultValue);
        macroSongSwitchingTimeLinker.setLimitMax(max);
        macroSongSwitchingTimeLinker.setLimitMin(min);
        macroSongSwitchingTimeLinker.setValue(value);
    }

    @Override
    public void setupMacroPostCaptureDelaySlider(int value, int defaultValue, int min, int max) {
        macroPostCaptureDelayLinker.setDefaultValue(defaultValue);
        macroPostCaptureDelayLinker.setLimitMax(max);
        macroPostCaptureDelayLinker.setLimitMin(min);
        macroPostCaptureDelayLinker.setValue(value);
    }

    @Override
    public void setupMacroKeyHoldTimeSlider(int value, int defaultValue, int min, int max) {
        macroKeyHoldTimeLinker.setDefaultValue(defaultValue);
        macroKeyHoldTimeLinker.setLimitMax(max);
        macroKeyHoldTimeLinker.setLimitMin(min);
        macroKeyHoldTimeLinker.setValue(value);
    }

    @Override
    public void setScannerAccountFileText(String value) {
        scannerAccountFileTextField.setText(value);
    }

    @Override
    public void setScannerStartKeyText(String value) {
        scannerStartKeyTextField.setText(value);
    }

    @Override
    public void setScannerStopKeyText(String value) {
        scannerStopKeyTextField.setText(value);
    }

    @Override
    public void setScannerCacheDirectoryText(String value) {
        scannerCacheDirectoryTextField.setText(value);
    }

    @Override
    public void setScannerAutoAnalysis(boolean value) {
        scannerAutoAnalysisToggleButton.setSelected(value);
    }

    @Override
    public void setupScannerAnalyzerThreadCountSlider(int value, int defaultValue, int min,
            int max) {
        scannerAnalyzerThreadCountLinker.setDefaultValue(defaultValue);
        scannerAnalyzerThreadCountLinker.setLimitMax(max);
        scannerAnalyzerThreadCountLinker.setLimitMin(min);
        scannerAnalyzerThreadCountLinker.setValue(value);

        new ThreadCountSliderSetter(max, 2).attachTo(scannerAnalyzerThreadCountSlider);
    }

    @Override
    public void setupScannerCaptureDelaySlider(int value, int defaultValue, int min, int max) {
        scannerCaptureDelayLinker.setDefaultValue(defaultValue);
        scannerCaptureDelayLinker.setLimitMax(max);
        scannerCaptureDelayLinker.setLimitMin(min);
        scannerCaptureDelayLinker.setValue(value);
    }

    @Override
    public void setupScannerKeyHoldTimeSlider(int value, int defaultValue, int min, int max) {
        scannerKeyHoldTimeLinker.setDefaultValue(defaultValue);
        scannerKeyHoldTimeLinker.setLimitMax(max);
        scannerKeyHoldTimeLinker.setLimitMin(min);
        scannerKeyHoldTimeLinker.setValue(value);
    }

    public static class ThreadCountSliderSetter extends StringConverter<Double> {
        private final int max;
        private final int scale;

        public ThreadCountSliderSetter(int max, int scale) {
            this.max = max;
            this.scale = scale;
        }

        public void attachTo(Slider slider) {
            slider.setBlockIncrement(1);
            slider.setLabelFormatter(this);
            slider.setMajorTickUnit(1);
            slider.setMax(max);
            slider.setMin(1);
            slider.setMinorTickCount(0);
            slider.setShowTickLabels(true);
            slider.setShowTickMarks(true);
        }

        private boolean shouldPrint(double value) {
            if (max >> scale == 0) {
                return true;
            } else if (value == 1 || value == max) {
                return true;
            }

            return value % (max >> scale) == 0;
        }

        @Override
        public String toString(Double object) {
            return shouldPrint(object.byteValue()) ? String.valueOf(object.intValue()) : "";
        }

        @Override
        public Double fromString(String string) {
            return 0.0;
        }
    }
}
