package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import java.nio.file.Path;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Window;

public interface LinkEditor {
    interface LinkEditorPresenter extends MvpPresenter<LinkEditorView> {
        StartData getStartData();

        void setStartData(StartData value);

        void onViewShown();

        ObservableList<CaptureData> onShowCaptureDataList(String pattern, boolean findAll);

        void onUpdateSearch(String pattern);

        Image onShowCaptureImage(int captureDataId);

        boolean onLinkCaptureData(int captureDataId);

        boolean onUnlinkCaptureData();
    }


    interface LinkEditorView extends MvpView<LinkEditorPresenter> {
        void requestStop();

        void showError(String header, Exception exception);

        boolean showConfirmation(String header, String content);

        void setMessageText(String text);

        void setSongText(String text);

        void showCaptureDataList(String pattern, boolean findAll);

        void updateSearch(String pattern);

        void showCaptureImage(int captureDataId);

        void linkCaptureData(int captureDataId);

        void unlinkCaptureData();
    }


    class StartData {
        public Path cacheDirectoryPath;
        public Runnable onLinkUpdate;
        public Window ownerWindow;
        public int songDataId;
    }
}
