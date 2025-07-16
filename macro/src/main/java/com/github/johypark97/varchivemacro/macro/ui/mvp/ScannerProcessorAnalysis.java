package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import java.util.List;

public interface ScannerProcessorAnalysis {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void runAnalysis_allCapture();

        void runAnalysis_selectedSong(List<Integer> selectedSongIdList);

        void showReviewView();
    }


    interface View
            extends Mvp.MvpView<View, Presenter>, ScannerProcessorFrame.ViewButtonController {
        void setProgress(double value);

        void setMessageText(String value);

        void setFunctionButton(String text, Runnable onAction);
    }
}
