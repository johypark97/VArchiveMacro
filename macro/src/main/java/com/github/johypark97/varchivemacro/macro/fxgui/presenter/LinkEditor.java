package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import java.nio.file.Path;
import java.util.List;
import javafx.scene.image.Image;
import javafx.stage.Window;

public interface LinkEditor {
    interface LinkEditorPresenter extends MvpPresenter<LinkEditorView> {
        StartData getStartData();

        void setStartData(StartData value);

        void onViewShown();

        List<CaptureData> onShowCaptureDataList(boolean findAll);

        Image onShowCaptureImage(int captureDataId);

        boolean onLinkCaptureData(int captureDataId);

        boolean onUnlinkCaptureData();
    }


    interface LinkEditorView extends MvpView<LinkEditorPresenter> {
        void onStop();

        void requestStop();

        void showError(String header, Throwable throwable);

        boolean showConfirmation(String header, String content);

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
