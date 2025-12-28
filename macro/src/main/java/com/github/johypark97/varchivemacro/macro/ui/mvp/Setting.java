package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.config.model.MacroClientMode;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.input.KeyEvent;

public interface Setting {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        boolean stopView();

        void apply();

        void revert();

        void close();

        void macro_onChangeClientMode(MacroClientMode value);

        void macro_onChangeUploadKey(KeyEvent event);

        void macro_onChangeStartUpKey(KeyEvent event);

        void macro_onChangeStartDownKey(KeyEvent event);

        void macro_onChangeStopKey(KeyEvent event);

        void macro_onChangeSongSwitchingTime(int value);

        void macro_onChangePostCaptureDelay(int value);

        void macro_onChangeKeyHoldTime(int value);

        void scanner_onChangeAccountFile(String value);

        void scanner_showAccountFileSelector();

        void scanner_onChangeStartKey(KeyEvent event);

        void scanner_onChangeStopKey(KeyEvent event);

        void scanner_onChangeCacheDirectory(String value);

        void scanner_showCacheDirectorySelector();

        void scanner_onChangeAutoAnalysis(boolean value);

        void scanner_onChangeAnalyzerThreadCount(int value);

        void scanner_onChangeCaptureDelay(int value);

        void scanner_onChangeKeyHoldTime(int value);

        void program_onChangePrereleaseNotification(boolean value);

        void program_onUseSystemProxy(boolean value);

        void program_onUseSystemCertificateStore(boolean value);
    }


    interface View extends Mvp.MvpView<View, Presenter> {
        void bindApplyButtonEnable(ObservableBooleanValue value);

        void bindRevertButtonEnable(ObservableBooleanValue value);

        void setMacroClientMode(MacroClientMode value);

        void setMacroUploadKeyText(String value);

        void setMacroStartUpKeyText(String value);

        void setMacroStartDownKeyText(String value);

        void setMacroStopKeyText(String value);

        void setupMacroSongSwitchingTimeSlider(int value, int defaultValue, int min, int max);

        void setupMacroPostCaptureDelaySlider(int value, int defaultValue, int min, int max);

        void setupMacroKeyHoldTimeSlider(int value, int defaultValue, int min, int max);

        void setScannerAccountFileText(String value);

        void setScannerStartKeyText(String value);

        void setScannerStopKeyText(String value);

        void setScannerCacheDirectoryText(String value);

        void setScannerAutoAnalysis(boolean value);

        void setupScannerAnalyzerThreadCountSlider(int value, int defaultValue, int min, int max);

        void setupScannerCaptureDelaySlider(int value, int defaultValue, int min, int max);

        void setupScannerKeyHoldTimeSlider(int value, int defaultValue, int min, int max);

        void setProgramPrereleaseNotification(boolean value);

        void setUseSystemProxy(boolean value);

        void setUseSystemCertificateStore(boolean value);
    }
}
