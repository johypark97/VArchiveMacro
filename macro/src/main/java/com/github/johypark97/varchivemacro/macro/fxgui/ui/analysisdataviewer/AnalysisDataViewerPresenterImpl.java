package com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer;

import com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer.AnalysisDataViewer.AnalysisDataViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer.AnalysisDataViewer.AnalysisDataViewerView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer.AnalysisDataViewer.RecordBoxData;
import com.github.johypark97.varchivemacro.macro.model.AnalyzedRecordData;
import com.github.johypark97.varchivemacro.macro.service.ScannerService;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import javafx.embed.swing.SwingFXUtils;

public class AnalysisDataViewerPresenterImpl implements AnalysisDataViewerPresenter {
    private final Runnable onStop;
    private final ScannerService scannerService;

    @MvpView
    public AnalysisDataViewerView view;

    public AnalysisDataViewerPresenterImpl(ScannerService scannerService, Runnable onStop) {
        this.onStop = onStop;
        this.scannerService = scannerService;
    }

    @Override
    public void onStartView() {
    }

    @Override
    public void onStopView() {
        view.getWindow().hide();

        onStop.run();
    }

    @Override
    public void showAnalysisData(Path cacheDirectoryPath, int analysisDataId) {
        AnalyzedRecordData data;
        try {
            data = scannerService.getAnalyzedRecordData(cacheDirectoryPath, analysisDataId);
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
