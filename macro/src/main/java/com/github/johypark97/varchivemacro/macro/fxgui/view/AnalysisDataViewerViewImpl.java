package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.AnalysisDataViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.AnalysisDataViewerView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.RecordBoxData;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.AnalysisDataViewerComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.AnalysisDataViewerStage;
import java.lang.ref.WeakReference;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class AnalysisDataViewerViewImpl
        extends AbstractMvpView<AnalysisDataViewerPresenter, AnalysisDataViewerView>
        implements AnalysisDataViewerView {
    private WeakReference<AnalysisDataViewerComponent> analysisDataViewerComponentReference;

    private AnalysisDataViewerComponent getAnalysisDataViewerComponent() {
        return analysisDataViewerComponentReference.get();
    }

    @Override
    public void showError(String header, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(getStage());

        alert.setHeaderText(header);
        alert.setContentText(throwable.toString());

        alert.showAndWait();
    }

    @Override
    public void setTitleData(Image image, String text) {
        getAnalysisDataViewerComponent().setTitleImage(image);
        getAnalysisDataViewerComponent().setTitleText(text);
    }

    @Override
    public void setRecordBoxData(int row, int column, RecordBoxData data) {
        getAnalysisDataViewerComponent().setRecordBoxData(row, column, data);
    }

    @Override
    protected Stage newStage() {
        AnalysisDataViewerStage stage = new AnalysisDataViewerStage();

        analysisDataViewerComponentReference =
                new WeakReference<>(stage.analysisDataViewerComponent);

        stage.initOwner(getPresenter().getStartData().ownerWindow);

        stage.setOnShowing(event -> {
            Stage source = (Stage) event.getSource();

            getAnalysisDataViewerComponent().setCloseButtonAction(
                    () -> getPresenter().stopPresenter());

            source.getScene().setOnKeyReleased(x -> {
                if (x.getCode() == KeyCode.ESCAPE) {
                    getPresenter().stopPresenter();
                }
            });

            getPresenter().updateView();
            getStage().sizeToScene();
        });

        return stage;
    }
}
