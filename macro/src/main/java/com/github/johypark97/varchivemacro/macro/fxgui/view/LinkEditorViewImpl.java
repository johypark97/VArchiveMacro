package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.LinkEditorPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.LinkEditorView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.LinkEditorComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.LinkEditorStage;
import java.awt.Toolkit;
import java.lang.ref.WeakReference;
import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Function;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LinkEditorViewImpl extends AbstractMvpView<LinkEditorPresenter, LinkEditorView>
        implements LinkEditorView {
    private static final Function<String, String> NORMALIZER = x -> Normalizer.normalize(
            TitleTool.normalizeTitle_recognition(x).toLowerCase(Locale.ENGLISH),
            Normalizer.Form.NFKD);

    private WeakReference<LinkEditorComponent> linkEditorComponentReference;

    private FilteredList<ScanDataManager.CaptureData> filteredCaptureDataList;

    private LinkEditorComponent getLinkEditorComponent() {
        return linkEditorComponentReference.get();
    }

    private void onUpdateSearch(String pattern) {
        if (pattern == null) {
            return;
        }

        String normalizedPattern = NORMALIZER.apply(pattern.trim());

        filteredCaptureDataList.setPredicate(
                x -> NORMALIZER.apply(x.scannedTitle.get()).contains(normalizedPattern));
    }

    @Override
    public void onStop() {
        filteredCaptureDataList = null; // NOPMD
    }

    @Override
    public void requestStop() {
        getPresenter().stopPresenter();
    }

    @Override
    public void showError(String header, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(getStage()).setHeaderText(header)
                .setContentText(throwable.toString()).setThrowable(throwable).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public boolean showConfirmation(String header, String content) {
        Alert alert = AlertBuilder.confirmation().setOwner(getStage()).setHeaderText(header)
                .setContentText(content).alert;

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
        filteredCaptureDataList = new FilteredList<>(
                FXCollections.observableArrayList(getPresenter().onShowCaptureDataList(findAll)));

        onUpdateSearch(pattern);

        getLinkEditorComponent().setCaptureDataList(filteredCaptureDataList);
    }

    @Override
    public void updateSearch(String pattern) {
        onUpdateSearch(pattern);
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
