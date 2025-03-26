package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonView;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public interface LinkEditor {
    interface LinkEditorPresenter extends CommonPresenter<LinkEditorView, LinkEditorPresenter> {
        void updateCaptureDataListFilter(String pattern);

        void showCaptureDataList(String pattern, boolean findAll);

        void showCaptureImage(int captureDataId);

        void linkCaptureData(int captureDataId);

        void unlinkCaptureData();
    }


    interface LinkEditorView extends CommonView<LinkEditorView, LinkEditorPresenter> {
        void startView();

        void showError(String header, Throwable throwable);

        boolean showConfirmation(String header, String content);

        void setSongText(String text);

        void setCaptureDataList(ObservableList<CaptureData> list);

        void setCaptureImage(Image image);
    }
}
