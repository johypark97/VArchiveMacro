package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.ScannerHomeViewModel;
import javafx.scene.control.TreeItem;

public interface ScannerHome {
    interface ScannerHomePresenter extends Mvp.MvpPresenter<ScannerHomeView, ScannerHomePresenter> {
        void startView();

        boolean stopView();

        void showRecordLoader();

        void showAccountFileSelector();

        void loadRemoteRecord();

        void updateSongFilter(String value);

        void showSong(int songId);

        void showScannerWindow();

        void showHome();
    }


    interface ScannerHomeView extends Mvp.MvpView<ScannerHomeView, ScannerHomePresenter> {
        void showViewer();

        void showLoader();

        void showProgress();

        String getDjNameText();

        String getAccountFileText();

        void setAccountFileText(String value);

        void setSongTreeViewRoot(TreeItem<ScannerHomeViewModel.SongTreeViewData> value);

        void showSongInformation(String title, String composer);

        void showSongRecord(ScannerHomeViewModel.SongRecord value);
    }
}
