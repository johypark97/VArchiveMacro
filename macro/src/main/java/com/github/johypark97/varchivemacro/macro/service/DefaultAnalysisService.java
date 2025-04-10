package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.macro.domain.AnalysisDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.NewRecordDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.ScanDataDomain;
import com.github.johypark97.varchivemacro.macro.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.model.AnalyzedRecordData;
import com.github.johypark97.varchivemacro.macro.model.RecordData;
import com.github.johypark97.varchivemacro.macro.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.service.task.AnalysisTask;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import javafx.concurrent.Task;

public class DefaultAnalysisService implements AnalysisService {
    private final ConfigRepository configRepository;

    private final AnalysisDataDomain analysisDataDomain;
    private final NewRecordDataDomain newRecordDataDomain;
    private final ScanDataDomain scanDataDomain;

    public DefaultAnalysisService(ConfigRepository configRepository,
            AnalysisDataDomain analysisDataDomain, NewRecordDataDomain newRecordDataDomain,
            ScanDataDomain scanDataDomain) {
        this.analysisDataDomain = analysisDataDomain;
        this.configRepository = configRepository;
        this.newRecordDataDomain = newRecordDataDomain;
        this.scanDataDomain = scanDataDomain;
    }

    @Override
    public boolean isReady_analysis() {
        return !scanDataDomain.isEmpty() && analysisDataDomain.isEmpty();
    }

    @Override
    public Task<Void> createTask_analysis(Runnable onDataReady) {
        ScannerConfig config = configRepository.getScannerConfig();

        return TaskManager.getInstance().register(AnalysisTask.class,
                new AnalysisTask(onDataReady, scanDataDomain, analysisDataDomain,
                        config.cacheDirectory, config.analysisThreadCount));
    }

    @Override
    public void stopTask_analysis() {
        TaskManager.Helper.cancel(AnalysisTask.class);
    }

    @Override
    public void clearAnalysisData(Runnable onClear) {
        if (TaskManager.getInstance().isRunningAny()) {
            return;
        }

        analysisDataDomain.clear();
        newRecordDataDomain.clear();

        onClear.run();
    }

    @Override
    public List<AnalysisData> copyAnalysisDataList() {
        return analysisDataDomain.copyAnalysisDataList();
    }

    @Override
    public AnalyzedRecordData getAnalyzedRecordData(int id) throws Exception {
        AnalyzedRecordData data = new AnalyzedRecordData();

        AnalysisData analysisData = analysisDataDomain.getAnalysisData(id);
        data.song = analysisData.songDataProperty().get().songProperty().get();

        String cacheDirectory = configRepository.getScannerConfig().cacheDirectory;
        BufferedImage image =
                scanDataDomain.getCaptureImage(analysisData.captureData.get().idProperty().get(),
                        cacheDirectory);

        Dimension resolution = new Dimension(image.getWidth(), image.getHeight());
        CollectionArea area = CollectionAreaFactory.create(resolution);

        data.titleImage = area.getTitle(image);
        data.titleText = analysisData.captureData.get().scannedTitle.get();

        for (Enums.Button button : Enums.Button.values()) {
            for (Enums.Pattern pattern : Enums.Pattern.values()) {
                data.rateImage[button.getWeight()][pattern.getWeight()] =
                        area.getRate(image, button, pattern);

                data.maxComboImage[button.getWeight()][pattern.getWeight()] =
                        area.getComboMark(image, button, pattern);

                RecordData recordData = analysisData.recordDataTable.get(button, pattern);
                if (recordData == null) {
                    continue;
                }

                data.rateText[button.getWeight()][pattern.getWeight()] = recordData.rateText.get();

                data.maxCombo[button.getWeight()][pattern.getWeight()] = recordData.maxCombo.get();
            }
        }

        return data;
    }
}
