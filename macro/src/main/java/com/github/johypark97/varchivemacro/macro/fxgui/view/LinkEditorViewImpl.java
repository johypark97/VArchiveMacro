package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.LinkEditorPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.LinkEditorView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.LinkEditorComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.LinkEditorStage;
import java.awt.Toolkit;
import java.lang.ref.WeakReference;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LinkEditorViewImpl extends AbstractMvpView<LinkEditorPresenter, LinkEditorView>
        implements LinkEditorView {
    private WeakReference<LinkEditorComponent> linkEditorComponentReference;

    private LinkEditorComponent getLinkEditorComponent() {
        return linkEditorComponentReference.get();
    }

    @Override
    public void requestStop() {
        getPresenter().stopPresenter();
    }

    @Override
    public void showError(String header, Exception exception) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(getStage());

        alert.setHeaderText(header);
        alert.setContentText(exception.toString());

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public boolean showConfirmation(String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(getStage());

        if (header != null) {
            alert.setHeaderText(header);
        }

        if (content != null) {
            alert.setContentText(content);
        }

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();

        return ButtonType.OK.equals(alert.getResult());
    }

    @Override
    public void setSongText(String text) {
        getLinkEditorComponent().setSongText(text);
    }

    @Override
    public void showCaptureDataList(String pattern, boolean findAll) {
        getLinkEditorComponent().setCaptureDataList(
                getPresenter().onShowCaptureDataList(pattern, findAll));
    }

    @Override
    public void updateSearch(String pattern) {
        getPresenter().onUpdateSearch(pattern);
    }

    @Override
    public void showCaptureImage(int captureDataId) {
        Image image = getPresenter().onShowCaptureImage(captureDataId);
        if (image != null) {
            getLinkEditorComponent().showImage(image);
        }
    }

    @Override
    public void linkCaptureData(int captureDataId) {
        if (getPresenter().onLinkCaptureData(captureDataId)) {
            getPresenter().stopPresenter();
        }
    }

    @Override
    public void unlinkCaptureData() {
        if (getPresenter().onUnlinkCaptureData()) {
            getPresenter().stopPresenter();
        }
    }

    @Override
    protected Stage newStage() {
        LinkEditorStage stage = new LinkEditorStage(this);

        linkEditorComponentReference = new WeakReference<>(stage.linkEditorComponent);

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(getPresenter().getStartData().ownerWindow);

        stage.setOnShown(event -> {
            getLinkEditorComponent().setSplitPaneDividerPositions(0.3);

            getPresenter().onViewShown();

            showCaptureDataList(null, false);
        });

        return stage;
    }
}
