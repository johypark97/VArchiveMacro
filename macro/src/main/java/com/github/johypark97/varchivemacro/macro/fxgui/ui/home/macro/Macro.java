package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel.AnalysisKey;

public interface Macro {
    interface MacroPresenter extends CommonPresenter<MacroView, MacroPresenter> {
        void start_up();

        void start_down();

        void stop();
    }


    interface MacroView extends MvpView<MacroView, MacroPresenter> {
        void startView();

        void stopView();

        AnalysisKey getAnalysisKey();

        void setAnalysisKey(AnalysisKey key);

        void setupCountSlider(int defaultValue, int limitMax, int limitMin, int value);

        int getCount();

        void setupCaptureDelaySlider(int defaultValue, int limitMax, int limitMin, int value);

        int getCaptureDelay();

        void setupCaptureDurationSlider(int defaultValue, int limitMax, int limitMin, int value);

        int getCaptureDuration();

        void setupKeyInputDurationSlider(int defaultValue, int limitMax, int limitMin, int value);

        int getKeyInputDuration();
    }
}
