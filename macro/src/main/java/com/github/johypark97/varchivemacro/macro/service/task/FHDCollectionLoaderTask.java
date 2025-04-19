package com.github.johypark97.varchivemacro.macro.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.ScanDataRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.service.CaptureImageCacheService;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FHDCollectionLoaderTask extends AbstractCollectionScanTask {
    private final String cacheDirectory;

    private CaptureImageCacheService captureImageCacheService;
    private CollectionArea collectionArea;

    public FHDCollectionLoaderTask(ScanDataRepository scanDataRepository,
            Map<String, List<Song>> categoryNameSongListMap, TitleTool titleTool,
            Set<String> selectedCategorySet, String cacheDirectory) {
        super(scanDataRepository, categoryNameSongListMap, titleTool, selectedCategorySet);

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
        return captureImageCacheService.read(data.idProperty().get());
    }

    @Override
    protected BufferedImage cropTitle(BufferedImage image) {
        return collectionArea.getTitle(image);
    }

    @Override
    protected Void callTask() throws Exception {
        captureImageCacheService = new CaptureImageCacheService(cacheDirectory);
        captureImageCacheService.validate();

        collectionArea = CollectionAreaFactory.create(new Dimension(1920, 1080));

        return super.callTask();
    }
}
