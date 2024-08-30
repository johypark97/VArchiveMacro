package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel.AnalyzedRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.AnalysisDataViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.AnalysisDataViewerView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.RecordBoxData;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import javafx.embed.swing.SwingFXUtils;

public class AnalysisDataViewerPresenterImpl implements AnalysisDataViewerPresenter {
    private WeakReference<ScannerModel> scannerModelReference;

    @MvpView
    public AnalysisDataViewerView view;

    public void linkModel(ScannerModel scannerModel) {
        scannerModelReference = new WeakReference<>(scannerModel);
    }

    private ScannerModel getScannerModel() {
        return scannerModelReference.get();
    }

    @Override
    public void showAnalysisData(Path cacheDirectoryPath, int analysisDataId) {
        AnalyzedRecordData data;
        try {
            data = getScannerModel().getAnalyzedRecordData(cacheDirectoryPath, analysisDataId);
        } catch (Exception e) {
            view.showError("Analyzed record data loading error", e);
            return;
        }

        view.setSongText(String.format("[%s] %s - %s", data.song.pack().name(), data.song.title(),
                data.song.composer()));
        view.setTitleImage(SwingFXUtils.toFXImage(data.titleImage, null));
        view.setTitleText(data.titleText);

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

                view.setRecordBoxData(i, j, recordBoxData);
            }
        }
    }
}
