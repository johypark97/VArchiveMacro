package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel.AnalyzedRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.AnalysisDataViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.AnalysisDataViewerView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.RecordBoxData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.StartData;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.Objects;
import javafx.embed.swing.SwingFXUtils;

public class AnalysisDataViewerPresenterImpl
        extends AbstractMvpPresenter<AnalysisDataViewerPresenter, AnalysisDataViewerView>
        implements AnalysisDataViewerPresenter {
    private WeakReference<ScannerModel> scannerModelReference;

    public StartData startData;

    public void linkModel(ScannerModel scannerModel) {
        scannerModelReference = new WeakReference<>(scannerModel);
    }

    private ScannerModel getScannerModel() {
        return scannerModelReference.get();
    }

    @Override
    public StartData getStartData() {
        return startData;
    }

    @Override
    public void setStartData(StartData value) {
        startData = value;
    }

    @Override
    public void updateView() {
        AnalyzedRecordData data;
        try {
            data = getScannerModel().getAnalyzedRecordData(startData.cacheDirectoryPath,
                    startData.analysisDataId);
        } catch (Exception e) {
            getView().showError("Analyzed record data loading error", e);
            return;
        }

        getView().setSongData(
                String.format("[%s] %s - %s", data.song.dlc, data.song.title, data.song.composer));
        getView().setTitleData(SwingFXUtils.toFXImage(data.titleImage, null), data.titleText);

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                RecordBoxData recordBoxData = new RecordBoxData();

                BufferedImage bufferedImage;

                bufferedImage = data.rateImage[i][j];
                if (bufferedImage != null) {
                    recordBoxData.rateImage = SwingFXUtils.toFXImage(bufferedImage, null);
                }

                bufferedImage = data.maxComboImage[i][j];
                if (bufferedImage != null) {
                    recordBoxData.maxComboImage = SwingFXUtils.toFXImage(bufferedImage, null);
                }

                recordBoxData.rateText = data.rateText[i][j];

                recordBoxData.maxCombo = data.maxCombo[i][j];

                getView().setRecordBoxData(i, j, recordBoxData);
            }
        }
    }

    @Override
    protected AnalysisDataViewerPresenter getInstance() {
        return this;
    }

    @Override
    protected boolean initialize() {
        Objects.requireNonNull(startData);
        Objects.requireNonNull(startData.cacheDirectoryPath);
        Objects.requireNonNull(startData.ownerWindow);

        return true;
    }
}
