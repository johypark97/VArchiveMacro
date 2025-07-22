package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import java.util.List;

public interface ScannerProcessorUpload {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        void collectNewRecord();

        void collectNewRecord(List<Integer> selectedSongIdList);

        void showAnalysisView();
    }


    interface View
            extends Mvp.MvpView<View, Presenter>, ScannerProcessorFrame.ViewButtonController {
    }
}
