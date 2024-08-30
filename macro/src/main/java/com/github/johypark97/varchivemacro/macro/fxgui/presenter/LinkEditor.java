package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import java.nio.file.Path;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public interface LinkEditor {
    interface LinkEditorPresenter extends MvpPresenter<LinkEditorView, LinkEditorPresenter> {
        void onStartView(Path cacheDirectoryPath, int songDataId, Runnable onUpdateLink);

        void updateCaptureDataListFilter(String pattern);

        void showCaptureDataList(String pattern, boolean findAll);

        void showCaptureImage(int captureDataId);

        void linkCaptureData(int captureDataId);

        void unlinkCaptureData();
    }


    interface LinkEditorView extends MvpView<LinkEditorView, LinkEditorPresenter> {
        void requestStopStage();

        void showError(String header, Throwable throwable);

        boolean showConfirmation(String header, String content);

        void setSongText(String text);

        void setCaptureDataList(ObservableList<CaptureData> list);

        void setCaptureImage(Image image);
    }
}
