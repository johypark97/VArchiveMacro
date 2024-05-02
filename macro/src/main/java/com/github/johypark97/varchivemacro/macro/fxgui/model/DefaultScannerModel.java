package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManagerHelper;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.RecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.CacheManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.ScannerService;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.task.AnalysisTask;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.task.CollectNewRecordTask;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.task.DefaultCollectionScanTask;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.task.UploadTask;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;

public class DefaultScannerModel implements ScannerModel {
    private final AnalysisDataManager analysisDataManager = new AnalysisDataManager();
    private final NewRecordDataManager newRecordDataManager = new NewRecordDataManager();
    private final ScanDataManager scanDataManager = new ScanDataManager();

    @Override
    public void validateCacheDirectory(Path path) throws IOException {
        new CacheManager(path).validate();
    }

    @Override
    public void setupService(Consumer<Throwable> onThrow) {
        ScannerService service = ServiceManager.getInstance().create(ScannerService.class);
        if (service == null) {
            throw new IllegalStateException("ScannerService has already been created.");
        }

        service.setOnFailed(event -> onThrow.accept(event.getSource().getException()));
    }

    @Override
    public void startCollectionScan(Runnable onDone, Runnable onCancel,
            Map<String, List<LocalDlcSong>> dlcTapSongMap, TitleTool titleTool,
            Set<String> selectedTabSet, Path cacheDirectoryPath, int captureDelay,
            int keyInputDuration) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task =
                    new DefaultCollectionScanTask(scanDataManager, dlcTapSongMap, titleTool,
                            selectedTabSet, cacheDirectoryPath, captureDelay, keyInputDuration);

            task.setOnCancelled(event -> onCancel.run());
            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        // service.setTaskConstructor(() -> {
        //     Task<Void> task = new FHDCollectionLoaderTask(scanDataManager, dlcTapSongMap, titleTool,
        //             selectedTabSet, cacheDirectoryPath);
        //
        //     task.setOnCancelled(event -> onCancel.run());
        //     task.setOnSucceeded(event -> onDone.run());
        //
        //     return task;
        // });

        service.reset();
        service.start();
    }

    @Override
    public void stopCollectionScan() {
        ServiceManagerHelper.stopService(ScannerService.class);
    }

    @Override
    public void starAnalysis(Runnable onDataReady, Runnable onDone, Runnable onCancel,
            Path cacheDirectoryPath) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task = new AnalysisTask(onDataReady, scanDataManager, analysisDataManager,
                    cacheDirectoryPath);

            task.setOnCancelled(event -> onCancel.run());
            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        service.reset();
        service.start();
    }

    @Override
    public void stopAnalysis() {
        ServiceManagerHelper.stopService(ScannerService.class);
    }

    @Override
    public void collectNewRecord(Runnable onDone, RecordModel recordModel) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task = new CollectNewRecordTask(recordModel, analysisDataManager,
                    newRecordDataManager);

            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        service.reset();
        service.start();
    }

    @Override
    public void startUpload(Runnable onDone, Runnable onCancel, DatabaseModel databaseModel,
            RecordModel recordModel, Path accountPath, int recordUploadDelay) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task =
                    new UploadTask(databaseModel, recordModel, newRecordDataManager, accountPath,
                            recordUploadDelay);

            task.setOnCancelled(event -> onCancel.run());
            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        service.reset();
        service.start();
    }

    @Override
    public void stopUpload() {
        ServiceManagerHelper.stopService(ScannerService.class);
    }

    @Override
    public boolean isScanDataEmpty() {
        return scanDataManager.isEmpty();
    }

    @Override
    public void clearScanData(Runnable onClear) {
        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));
        if (service.isRunning()) {
            return;
        }

        scanDataManager.clear();

        onClear.run();
    }

    @Override
    public boolean isAnalysisDataEmpty() {
        return analysisDataManager.isEmpty();
    }

    @Override
    public void clearAnalysisData(Runnable onClear) {
        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));
        if (service.isRunning()) {
            return;
        }

        analysisDataManager.clear();
        newRecordDataManager.clear();

        onClear.run();
    }

    @Override
    public boolean isNewRecordDataEmpty() {
        return newRecordDataManager.isEmpty();
    }

    @Override
    public ObservableMap<Integer, CaptureData> getObservableCaptureDataMap() {
        return scanDataManager.captureDataMapProperty();
    }

    @Override
    public ObservableMap<Integer, SongData> getObservableSongDataMap() {
        return scanDataManager.songDataMapProperty();
    }

    @Override
    public BufferedImage getCaptureImage(Path cacheDirectoryPath, int id) throws IOException {
        CaptureData captureData = scanDataManager.captureDataMapProperty().get(id);

        try {
            return new CacheManager(cacheDirectoryPath).read(id);
        } catch (IOException e) {
            captureData.exception.set(e);
            throw e;
        }
    }

    @Override
    public ObservableMap<Integer, AnalysisData> getObservableAnalysisDataMap() {
        return analysisDataManager.analysisDataMapProperty();
    }

    @Override
    public AnalyzedRecordData getAnalyzedRecordData(Path cacheDirectoryPath, int id)
            throws Exception {
        AnalyzedRecordData data = new AnalyzedRecordData();

        AnalysisData analysisData = analysisDataManager.analysisDataMapProperty().get(id);
        data.song = analysisData.songDataProperty().get().songProperty().get();

        BufferedImage image = getCaptureImage(cacheDirectoryPath,
                analysisData.captureData.get().idProperty().get());
        Dimension resolution = new Dimension(image.getWidth(), image.getHeight());
        CollectionArea area = CollectionAreaFactory.create(resolution);

        data.titleImage = area.getTitle(image);
        data.titleText = analysisData.captureData.get().scannedTitle.get();

        for (Button button : Button.values()) {
            for (Pattern pattern : Pattern.values()) {
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

    @Override
    public ObservableMap<Integer, NewRecordData> getObservableNewRecordDataMap() {
        return newRecordDataManager.newRecordDataMapProperty();
    }
}
