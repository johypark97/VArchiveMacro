package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.model.AnalyzedRecordData;
import com.github.johypark97.varchivemacro.macro.provider.ServiceProvider;
import com.github.johypark97.varchivemacro.macro.service.AnalysisService;
import com.github.johypark97.varchivemacro.macro.ui.presenter.AnalysisDataViewer.AnalysisDataViewerPresenter;
import com.github.johypark97.varchivemacro.macro.ui.presenter.AnalysisDataViewer.AnalysisDataViewerView;
import com.github.johypark97.varchivemacro.macro.ui.presenter.AnalysisDataViewer.RecordBoxData;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;

public class AnalysisDataViewerPresenterImpl implements AnalysisDataViewerPresenter {
    private final ServiceProvider serviceProvider;

    private final Runnable onStop;

    @MvpView
    public AnalysisDataViewerView view;

    public AnalysisDataViewerPresenterImpl(ServiceProvider serviceProvider, Runnable onStop) {
        this.serviceProvider = serviceProvider;

        this.onStop = onStop;
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
    public void showAnalysisData(int analysisDataId) {
        AnalysisService analysisService = serviceProvider.getAnalysisService();

        AnalyzedRecordData data;
        try {
            data = analysisService.getAnalyzedRecordData(analysisDataId);
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
