package com.github.johypark97.varchivemacro.macro.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.CacheManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FHDCollectionLoaderTask extends AbstractCollectionScanTask {
    private final CacheManager cacheManager;

    private CollectionArea collectionArea;

    public FHDCollectionLoaderTask(ScanDataManager scanDataManager,
            Map<String, List<LocalDlcSong>> dlcTapSongMap, TitleTool titleTool,
            Set<String> selectedTabSet, Path cacheDirectoryPath) {
        super(scanDataManager, dlcTapSongMap, titleTool, selectedTabSet);

        cacheManager = new CacheManager(cacheDirectoryPath);
    }

    @Override
    protected void moveToNextTab() {
    }

    @Override
    protected void moveToNextSong() {
    }

    @Override
    protected void checkCacheDirectory() throws IOException {
        cacheManager.validate();
    }

    @Override
    protected BufferedImage captureScreenshot(CaptureData data) throws Exception {
        return cacheManager.read(data.id);
    }

    @Override
    protected BufferedImage cropTitle(BufferedImage image) {
        return collectionArea.getTitle(image);
    }

    @Override
    protected Void callTask() throws Exception {
        collectionArea = CollectionAreaFactory.create(new Dimension(1920, 1080));

        return super.callTask();
    }
}
