package com.github.johypark97.varchivemacro.macro.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.StringUtils;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.LinkMetadata;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ocr.TitleOcr;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCollectionScanTask extends InterruptibleTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCollectionScanTask.class);

    private static final String CATEGORY_CLEAR_PASS_PLUS = "CLEARPASS+";
    private static final int DUPLICATE_LIMIT = 2;

    private final Map<String, List<Song>> categoryNameSongListMap;
    private final Set<String> selectedCategorySet;
    private final TitleTool titleTool;

    private final WeakReference<ScanDataManager> scanDataManagerReference;

    public AbstractCollectionScanTask(ScanDataManager scanDataManager,
            Map<String, List<Song>> categoryNameSongListMap, TitleTool titleTool,
            Set<String> selectedCategorySet) {
        this.categoryNameSongListMap = categoryNameSongListMap;
        this.selectedCategorySet = selectedCategorySet;
        this.titleTool = titleTool;

        scanDataManagerReference = new WeakReference<>(scanDataManager);
    }

    private static String normalizeTitle(String value) {
        return TitleTool.normalizeTitle_recognition(value);
    }

    protected abstract void moveToNextCategory() throws InterruptedException;

    protected abstract void moveToNextSong() throws InterruptedException;

    protected abstract void checkCacheDirectory() throws IOException;

    protected abstract BufferedImage captureScreenshot(CaptureData data) throws Exception;

    protected abstract BufferedImage cropTitle(BufferedImage image);

    private ScanDataManager getScanDataManager() {
        return scanDataManagerReference.get();
    }

    private String normalizeSongTitle(Song song) {
        return normalizeTitle(titleTool.getClippedTitleOrDefault(song.id(), song.title()));
    }

    private String normalizeScannedTitle(String value) {
        return titleTool.remapScannedTitle(normalizeTitle(value));
    }

    private Queue<Entry<String, List<Song>>> createCaptureQueue(
            Map<String, List<Song>> categoryNameSongListMap) {
        Queue<Entry<String, List<Song>>> queue = new LinkedList<>();

        categoryNameSongListMap.forEach((categoryName, songList) -> {
            // add an empty map before the ClearPass+ category to skip the favorite category
            if (CATEGORY_CLEAR_PASS_PLUS.equals(categoryName)) {
                queue.add(Map.entry(CATEGORY_CLEAR_PASS_PLUS, List.of()));
            }

            queue.add(Map.entry(categoryName,
                    selectedCategorySet.contains(categoryName) ? songList : List.of()));
        });

        return queue;
    }

    private Map<String, List<SongData>> prepareSongData(List<Song> songList) {
        Map<String, List<SongData>> map = new HashMap<>();

        songList.forEach(song -> {
            String normalizedTitle = normalizeSongTitle(song);
            SongData data = getScanDataManager().createSongData(song, normalizedTitle);
            map.computeIfAbsent(normalizedTitle, x -> new LinkedList<>()).add(data);
        });

        return map;
    }

    private String readTitle(OcrWrapper ocr, BufferedImage image) throws IOException, PixError {
        byte[] titleImageBytes = ImageConverter.imageToPngBytes(cropTitle(image));

        try (PixWrapper pix = new PixWrapper(titleImageBytes)) {
            PixPreprocessor.preprocessTitle(pix);

            return ocr.run(pix.pixInstance);
        }
    }

    private List<SongData> findExactMatch(Map<String, List<SongData>> lookup, String titleKey) {
        return lookup.get(titleKey);
    }

    private Map<String, List<SongData>> findSimilarMatch(Map<String, List<SongData>> lookup,
            String titleKey) {
        Set<String> similarKeySet = new HashSet<>();
        double maximumSimilarity = -1;

        for (String lookupKey : lookup.keySet()) {
            double similarity = new StringUtils.StringDiff(titleKey, lookupKey).getSimilarity();

            if (similarity >= maximumSimilarity) {
                if (similarity > maximumSimilarity) {
                    maximumSimilarity = similarity;
                    similarKeySet.clear();
                }
                similarKeySet.add(lookupKey);
            }
        }

        return lookup.entrySet().stream().filter(x -> similarKeySet.contains(x.getKey()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private boolean linkSongAndCapture(SongData songData, CaptureData captureData) {
        for (CaptureData child : songData.childListProperty()) {
            if (child.parentListProperty().size() == 1) {
                if (child.scannedTitle.get().equals(captureData.scannedTitle.get())) {
                    return false;
                }
            }
        }

        songData.link(captureData);

        LinkMetadata linkMetadata = songData.linkMapProperty().get(captureData);
        boolean linkExact = songData.childListProperty().size() == 1
                && linkMetadata.distanceProperty().get() == 0;
        songData.linkExact.set(linkExact);
        songData.selected.set(linkExact);

        return true;
    }

    private void findAndLinkSongAndCapture(Map<String, List<SongData>> lookup,
            CaptureData captureData) {
        String captureDataTitle = captureData.scannedTitle.get();
        LOGGER.atDebug().log("[findAndLinkSongAndCapture()] {}", captureData);

        // check if there is an exact match
        {
            List<SongData> list = findExactMatch(lookup, captureDataTitle);
            if (list != null) {
                list.forEach(x -> {
                    if (!linkSongAndCapture(x, captureData)) {
                        LOGGER.atDebug().log("[exact found - link skipped] {}", x);
                    } else {
                        LOGGER.atDebug().log("[exact found - linked] {}, linked: {}", x,
                                x.linkMapProperty().get());
                    }
                });

                return;
            }
        }

        // check if there are similar matches
        {
            Map<String, List<SongData>> map = findSimilarMatch(lookup, captureDataTitle);

            // link if the count of similar matches is less than or equal to the duplicate limit
            if (map.size() <= DUPLICATE_LIMIT) {
                map.values().forEach(x -> x.forEach(y -> {
                    if (!linkSongAndCapture(y, captureData)) {
                        LOGGER.atDebug().log("[similar found - link skipped] {}", y);
                    } else {
                        LOGGER.atDebug().log("[similar found - linked] {}, linked: {}", y,
                                y.linkMapProperty().get());
                    }
                }));

                return;
            }
        }

        LOGGER.atDebug().log("[not found] {}", captureData);
    }

    @Override
    protected Void callTask() throws Exception {
        // throw an exception if there are previous capture data
        if (!getScanDataManager().isEmpty()) {
            throw new IllegalStateException("ScanDataManager is not clean");
        }

        // check the cache directory
        checkCacheDirectory();

        // create queue that filtered by selectedCategorySet
        Queue<Entry<String, List<Song>>> captureQueue = createCaptureQueue(categoryNameSongListMap);

        // run main task
        try (OcrWrapper ocr = new TitleOcr()) {
            while (true) {
                Entry<String, List<Song>> tabEntry = captureQueue.poll();
                if (tabEntry == null) {
                    break;
                }

                moveToNextCategory();

                if (tabEntry.getValue().isEmpty()) {
                    continue;
                }

                // register song data and create normalized title - song data list lookup
                Map<String, List<SongData>> lookupNormalizedTitleSongDataList =
                        prepareSongData(tabEntry.getValue());

                String previousTitle = "";
                boolean firstCapture = true;
                int duplicateCount = 0;
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    if (firstCapture) {
                        firstCapture = false;
                    } else {
                        moveToNextSong();
                    }

                    // create capture data
                    CaptureData data = getScanDataManager().createCaptureData();

                    // capture the screen
                    BufferedImage image = captureScreenshot(data);

                    // scan the title from the image
                    String scannedTitle = readTitle(ocr, image);
                    scannedTitle = normalizeScannedTitle(scannedTitle);
                    data.scannedTitle.set(scannedTitle);

                    // find and link the song
                    findAndLinkSongAndCapture(lookupNormalizedTitleSongDataList, data);

                    // check duplication and break the loop
                    if (!scannedTitle.equals(previousTitle)) {
                        duplicateCount = 0;
                    } else {
                        ++duplicateCount;

                        if (duplicateCount >= DUPLICATE_LIMIT) {
                            break;
                        }
                    }

                    // store scannedTitle as the previousTitle
                    previousTitle = scannedTitle;
                }
            }
        } catch (InterruptedException ignored) {
        }

        // return to the ALL tab
        try {
            int count = captureQueue.size() + 1;
            for (int i = 0; i < count; ++i) {
                moveToNextCategory();
            }
        } catch (InterruptedException ignored) {
        }

        return null;
    }
}
