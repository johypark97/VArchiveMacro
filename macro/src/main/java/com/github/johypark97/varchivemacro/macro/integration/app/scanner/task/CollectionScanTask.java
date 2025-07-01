package com.github.johypark97.varchivemacro.macro.integration.app.scanner.task;

import com.github.johypark97.varchivemacro.lib.scanner.StringUtils;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureBound;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.app.SongCaptureLinkService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.common.InterruptibleTask;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CollectionScanTask extends InterruptibleTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionScanTask.class);

    private static final String CATEGORY_NAME_CLEAR_PASS_PLUS = "CLEARPASS+";
    private static final int CAPTURE_DUPLICATE_LIMIT = 2;
    private static final int LINK_DUPLICATE_LIMIT = 2;

    private final CaptureService captureService;
    private final PixImageService pixImageService;
    private final SongCaptureLinkService songCaptureLinkService;
    private final SongService songService;
    private final SongTitleService songTitleService;

    private final OcrServiceFactory songTitleOcrServiceFactory;

    private final Set<String> selectedCategorySet;

    public CollectionScanTask(CaptureService captureService, PixImageService pixImageService,
            SongCaptureLinkService songCaptureLinkService, SongService songService,
            SongTitleService songTitleService, OcrServiceFactory songTitleOcrServiceFactory,
            Set<String> selectedCategorySet) {
        this.captureService = captureService;
        this.pixImageService = pixImageService;
        this.songCaptureLinkService = songCaptureLinkService;
        this.songService = songService;
        this.songTitleService = songTitleService;

        this.songTitleOcrServiceFactory = songTitleOcrServiceFactory;

        this.selectedCategorySet = selectedCategorySet;
    }

    protected abstract BufferedImage captureScreen() throws InterruptedException;

    protected abstract CaptureBound getTitleBound();

    protected abstract void moveToNextCategory() throws InterruptedException;

    protected abstract void moveToNextSong() throws InterruptedException;

    protected abstract void writeImage(int captureId, BufferedImage captureImage)
            throws InterruptedException;

    private Queue<List<Song>> createCategoryQueue() {
        Queue<List<Song>> queue = new LinkedList<>();

        songService.groupSongByCategory().forEach((category, songList) -> {
            if (CATEGORY_NAME_CLEAR_PASS_PLUS.equals(category.name())) {
                queue.add(List.of());
            }

            queue.add(selectedCategorySet.contains(category.name()) ? songList : List.of());
        });

        return queue;
    }

    private String readTitle(OcrService ocrService, BufferedImage image, CaptureBound titleBound)
            throws IOException, PixImageException {
        BufferedImage titleImage =
                image.getSubimage(titleBound.x(), titleBound.y(), titleBound.width(),
                        titleBound.height());

        try (PixImage pix = pixImageService.createPixImage(titleImage)) {
            pixImageService.preprocessTitle(pix);
            return ocrService.run(pix);
        }
    }

    private Map<String, List<Song>> createLookup(List<Song> songList) {
        return songList.stream().collect(Collectors.groupingBy(
                song -> songTitleService.normalizeTitle(
                        songTitleService.getClippedTitleOrDefault(song))));
    }

    private void findAndLinkSongAndCaptureEntry(Map<String, List<Song>> lookup,
            CaptureEntry captureEntry) {
        String captureImageTitle = captureEntry.capture().scannedTitle;
        LOGGER.atTrace().log("[findAndLinkSongAndCaptureEntry()] {}", captureImageTitle);

        // check if there is an exact match
        {
            List<Song> list = findExactMatch(lookup, captureImageTitle);
            if (list != null) {
                list.forEach(song -> {
                    if (!linkSongAndCapture(song, captureEntry, captureImageTitle)) {
                        LOGGER.atTrace().log("[exact found - link skipped] {}", song);
                    } else {
                        LOGGER.atTrace().log("[exact found - linked] {}", song);

                        // Due to the behavior of the linkSongAndCapture() method, the return value
                        // of the groupBySong().get() method is guaranteed to be non-null.
                        List<CaptureEntry> linkedCaptureEntryList =
                                songCaptureLinkService.groupBySong().get(song).values().stream()
                                        .map(SongCaptureLink::captureEntry).toList();
                        LOGGER.atTrace().log("[linked] {}", linkedCaptureEntryList);
                    }
                });

                return;
            }
        }

        // check if there are similar matches
        {
            Map<String, List<Song>> map = findSimilarMatch(lookup, captureImageTitle);
            if (map.size() <= LINK_DUPLICATE_LIMIT) {
                for (Map.Entry<String, List<Song>> entry : map.entrySet()) {
                    String normalizedTitle = entry.getKey();

                    entry.getValue().forEach(song -> {
                        if (!linkSongAndCapture(song, captureEntry, normalizedTitle)) {
                            LOGGER.atTrace().log("[similar found - link skipped] {}", song);
                        } else {
                            LOGGER.atTrace().log("[similar found - linked] {}", song);

                            // Due to the behavior of the linkSongAndCapture() method, the return
                            // value of the groupBySong().get() method is guaranteed to be non-null.
                            List<CaptureEntry> linkedCaptureEntryList =
                                    songCaptureLinkService.groupBySong().get(song).values().stream()
                                            .map(SongCaptureLink::captureEntry).toList();
                            LOGGER.atTrace().log("[linked] {}", linkedCaptureEntryList);
                        }
                    });
                }
            }
        }

        LOGGER.atTrace().log("[not found] {}", captureImageTitle);
    }

    private List<Song> findExactMatch(Map<String, List<Song>> lookup, String scannedTitle) {
        return lookup.get(scannedTitle);
    }

    private Map<String, List<Song>> findSimilarMatch(Map<String, List<Song>> lookup,
            String scannedTitle) {
        Set<String> similarKeySet = new HashSet<>();
        double maximumSimilarity = -1;

        for (String lookupKey : lookup.keySet()) {
            double similarity = new StringUtils.StringDiff(scannedTitle, lookupKey).getSimilarity();

            if (similarity >= maximumSimilarity) {
                if (similarity > maximumSimilarity) {
                    maximumSimilarity = similarity;
                    similarKeySet.clear();
                }
                similarKeySet.add(lookupKey);
            }
        }

        return lookup.entrySet().stream().filter(x -> similarKeySet.contains(x.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean linkSongAndCapture(Song song, CaptureEntry captureEntry,
            String normalizedSongTitle) {
        // Get all captures already linked with the song.
        Map<CaptureEntry, SongCaptureLink> alreadyLinkedCaptureMap =
                songCaptureLinkService.groupBySong().get(song);

        // If at least one capture is already linked.
        if (alreadyLinkedCaptureMap != null) {
            List<SongCaptureLink> alreadyLinkedCaptureList =
                    alreadyLinkedCaptureMap.values().stream().toList();

            // If there is only a capture that perfectly matches the song, skip linking.
            for (SongCaptureLink link : alreadyLinkedCaptureList) {
                // The return value of the groupByCaptureEntry().get() method is guaranteed to be
                // non-null.
                if (songCaptureLinkService.groupByCaptureEntry().get(link.captureEntry()).size()
                        == 1) {
                    if (link.distance() == 0) {
                        return false;
                    }
                }
            }
        }

        StringUtils.StringDiff diff = new StringUtils.StringDiff(normalizedSongTitle,
                captureEntry.capture().scannedTitle);
        songCaptureLinkService.save(
                new SongCaptureLink(song, captureEntry, diff.getDistance(), diff.getSimilarity()));

        return true;
    }

    @Override
    protected Void callTask() throws Exception {
        if (!captureService.isEmpty()) {
            throw new IllegalStateException();
        }

        songCaptureLinkService.deleteAll();

        // create a queue that filtered by selectedCategorySet
        Queue<List<Song>> captureQueue = createCategoryQueue();

        CaptureBound titleBound = getTitleBound();

        // run main task
        try (OcrService ocrService = songTitleOcrServiceFactory.create()) {
            while (!captureQueue.isEmpty()) {
                List<Song> songList = captureQueue.poll();

                moveToNextCategory();

                if (songList.isEmpty()) {
                    continue;
                }

                // create a song list lookup grouped by normalized title
                Map<String, List<Song>> lookup = createLookup(songList);

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

                    // capture the screen
                    BufferedImage captureImage = captureScreen();

                    // read title
                    String scannedTitle = readTitle(ocrService, captureImage, titleBound);
                    scannedTitle = songTitleService.normalizeTitle(scannedTitle);

                    // store cache data and image
                    Capture capture = new Capture(scannedTitle, titleBound);
                    CaptureEntry captureEntry = captureService.save(capture);

                    writeImage(captureEntry.entryId(), captureImage);

                    // find and link the song
                    findAndLinkSongAndCaptureEntry(lookup, captureEntry);

                    // check duplication and break the loop
                    if (!scannedTitle.equals(previousTitle)) {
                        duplicateCount = 0;
                    } else {
                        duplicateCount++;

                        if (duplicateCount >= CAPTURE_DUPLICATE_LIMIT) {
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
            for (int i = 0; i < count; i++) {
                moveToNextCategory();
            }
        } catch (InterruptedException ignored) {
        }

        return null;
    }
}
