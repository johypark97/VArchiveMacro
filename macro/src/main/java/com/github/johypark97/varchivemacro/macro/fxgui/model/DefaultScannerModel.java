package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManagerHelper;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.CacheManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.ScannerService;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.task.DefaultCollectionScanTask;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class DefaultScannerModel implements ScannerModel {
    private final ScanDataManager scanDataManager = new ScanDataManager();

    @Override
    public void validateCacheDirectory(Path path) throws IOException {
        new CacheManager(path).validate();
    }

    @Override
    public void setupService(Runnable onDone, Runnable onCancel, Consumer<Throwable> onThrow) {
        ScannerService service = ServiceManager.getInstance().create(ScannerService.class);
        if (service == null) {
            throw new IllegalStateException("ScannerService has already been created.");
        }

        service.setOnCancelled(event -> onCancel.run());
        service.setOnFailed(event -> onThrow.accept(event.getSource().getException()));
        service.setOnSucceeded(event -> onDone.run());
    }

    @Override
    public boolean startCollectionScan(Map<String, List<LocalDlcSong>> dlcTapSongMap,
            TitleTool titleTool, Set<String> selectedTabSet, Path cacheDirectoryPath,
            int captureDelay, int keyInputDuration) {
        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));
        if (service.isRunning()) {
            return false;
        }

        service.setTaskConstructor(
                () -> new DefaultCollectionScanTask(scanDataManager, dlcTapSongMap, titleTool,
                        selectedTabSet, cacheDirectoryPath, captureDelay, keyInputDuration));

        // service.setTaskConstructor(
        //         () -> new FHDCollectionLoaderTask(scanDataManager, dlcTapSongMap, titleTool,
        //                 selectedTabSet, cacheDirectoryPath));

        service.reset();
        service.start();

        return true;
    }

    @Override
    public boolean stopCollectionScan() {
        return ServiceManagerHelper.stopService(ScannerService.class);
    }
}
