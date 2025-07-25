package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerUploadViewModel;
import java.util.List;
import javafx.collections.ObservableList;

public interface ScannerProcessorUpload {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        void collectNewRecord();

        void collectNewRecord(List<Integer> selectedSongIdList);

        void upload();

        void showAnalysisView();
    }


    interface View
            extends Mvp.MvpView<View, Presenter>, ScannerProcessorFrame.ViewButtonController {
        void setRecordTableItemList(ObservableList<ScannerUploadViewModel.NewRecordData> value);

        void updateSelectedCountText();

        void showProgressBox();

        void hideProgressBox();

        void setProgress(double value);
    }
}
