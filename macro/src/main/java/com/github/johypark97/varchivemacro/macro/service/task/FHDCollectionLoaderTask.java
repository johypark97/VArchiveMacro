package com.github.johypark97.varchivemacro.macro.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.domain.CacheManager;
import com.github.johypark97.varchivemacro.macro.domain.ScanDataDomain;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FHDCollectionLoaderTask extends AbstractCollectionScanTask {
    private final String cacheDirectory;

    private CacheManager cacheManager;
    private CollectionArea collectionArea;

    public FHDCollectionLoaderTask(ScanDataDomain scanDataDomain,
            Map<String, List<Song>> categoryNameSongListMap, TitleTool titleTool,
            Set<String> selectedCategorySet, String cacheDirectory) {
        super(scanDataDomain, categoryNameSongListMap, titleTool, selectedCategorySet);

        this.cacheDirectory = cacheDirectory;
    }

    @Override
    protected void moveToNextCategory() {
    }

    @Override
    protected void moveToNextSong() {
    }

    @Override
    protected BufferedImage captureScreenshot(CaptureData data) throws Exception {
        return cacheManager.read(data.idProperty().get());
    }

    @Override
    protected BufferedImage cropTitle(BufferedImage image) {
        return collectionArea.getTitle(image);
    }

    @Override
    protected Void callTask() throws Exception {
        cacheManager = new CacheManager(cacheDirectory);
        cacheManager.validate();

        collectionArea = CollectionAreaFactory.create(new Dimension(1920, 1080));

        return super.callTask();
    }
}
