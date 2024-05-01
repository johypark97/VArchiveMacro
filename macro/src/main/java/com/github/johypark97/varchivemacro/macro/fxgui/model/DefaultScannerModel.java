package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManagerHelper;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.CacheManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.ScannerService;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.task.DefaultCollectionScanTask;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class DefaultScannerModel implements ScannerModel {
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
    public boolean startCollectionScan(Runnable onStart, Runnable onDone, Runnable onCancel,
            Map<String, List<LocalDlcSong>> dlcTapSongMap, TitleTool titleTool,
            Set<String> selectedTabSet, Path cacheDirectoryPath, int captureDelay,
            int keyInputDuration) {
        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));
        if (service.isRunning()) {
            return false;
        }

        service.setTaskConstructor(() -> {
            Task<Void> task =
                    new DefaultCollectionScanTask(scanDataManager, dlcTapSongMap, titleTool,
                            selectedTabSet, cacheDirectoryPath, captureDelay, keyInputDuration);

            task.setOnCancelled(event -> onCancel.run());
            task.setOnScheduled(event -> onStart.run());
            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        // service.setTaskConstructor(() -> {
        //     Task<Void> task = new FHDCollectionLoaderTask(scanDataManager, dlcTapSongMap, titleTool,
        //             selectedTabSet, cacheDirectoryPath);
        //
        //     task.setOnCancelled(event -> onCancel.run());
        //     task.setOnScheduled(event -> onStart.run());
        //     task.setOnSucceeded(event -> onDone.run());
        //
        //     return task;
        // });

        service.reset();
        service.start();

        return true;
    }

    @Override
    public boolean stopCollectionScan() {
        return ServiceManagerHelper.stopService(ScannerService.class);
    }

    @Override
    public boolean isScanDataEmpty() {
        return scanDataManager.isEmpty();
    }

    @Override
    public void clearScanData() {
        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));
        if (!service.isRunning()) {
            scanDataManager.clear();
        }
    }

    @Override
    public ObservableList<CaptureData> getObservableCaptureDataList() {
        return scanDataManager.captureDataListProperty();
    }

    @Override
    public ObservableList<SongData> getObservableSongDataList() {
        return scanDataManager.songDataListProperty();
    }

    @Override
    public BufferedImage getCaptureImage(Path cacheDirectoryPath, int id) throws IOException {
        CaptureData captureData = scanDataManager.captureDataListProperty().get(id);

        try {
            return new CacheManager(cacheDirectoryPath).read(id);
        } catch (IOException e) {
            captureData.exception.set(e);
            throw e;
        }
    }
}
