package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerAnalysisViewModel;
import java.util.List;
import javafx.collections.ObservableList;

public interface ScannerProcessorAnalysis {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        void runAnalysis_allCapture();

        void runAnalysis_selectedSong(List<Integer> selectedSongIdList);

        void showResult();

        void hideResult();

        void showResultException(Exception exception);

        void showReviewView();
    }


    interface View
            extends Mvp.MvpView<View, Presenter>, ScannerProcessorFrame.ViewButtonController {
        void setProgress(double value);

        void setMessageText(String value);

        void setFunctionButton(String text, Runnable onAction);

        void enableShowResultButton(boolean value);

        void showResult();

        void hideResult();

        void setResultTableItemList(ObservableList<ScannerAnalysisViewModel.AnalysisResult> value);
    }
}
