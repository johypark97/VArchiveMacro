package com.github.johypark97.varchivemacro.macro.integration.app.scanner.task;

import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.StringUtils;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureBound;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.common.InterruptibleTask;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.OcrFactory;
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

    private final CaptureRepository captureRepository;
    private final SongCaptureLinkRepository songCaptureLinkRepository;
    private final SongService songService;
    private final SongTitleService songTitleService;

    private final OcrFactory songTitleOcrFactory;

    private final Set<String> selectedCategorySet;

    public CollectionScanTask(CaptureRepository captureRepository,
            SongCaptureLinkRepository songCaptureLinkRepository, SongService songService,
            SongTitleService songTitleService, OcrFactory songTitleOcrFactory,
            Set<String> selectedCategorySet) {
        this.captureRepository = captureRepository;
        this.songCaptureLinkRepository = songCaptureLinkRepository;
        this.songService = songService;
        this.songTitleService = songTitleService;

        this.songTitleOcrFactory = songTitleOcrFactory;

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

    private String readTitle(OcrWrapper ocr, BufferedImage image, CaptureBound titleBound)
            throws IOException, PixError {
        BufferedImage titleImage =
                image.getSubimage(titleBound.x(), titleBound.y(), titleBound.width(),
                        titleBound.height());

        byte[] titleImageBytes = ImageConverter.imageToPngBytes(titleImage);
        try (PixWrapper pix = new PixWrapper(titleImageBytes)) {
            PixPreprocessor.preprocessTitle(pix);
            return ocr.run(pix.pixInstance);
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

                        List<CaptureEntry> linkedCaptureEntryList =
                                songCaptureLinkRepository.groupBySong().get(song).values().stream()
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

                            List<CaptureEntry> linkedCaptureEntryList =
                                    songCaptureLinkRepository.groupBySong().get(song).values()
                                            .stream().map(SongCaptureLink::captureEntry).toList();
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
        List<SongCaptureLink> alreadyLinkedCaptureList =
                songCaptureLinkRepository.groupBySong().get(song).values().stream().toList();

        for (SongCaptureLink link : alreadyLinkedCaptureList) {
            if (songCaptureLinkRepository.groupByCaptureEntry().get(link.captureEntry()).size()
                    == 1) {
                if (link.distance() == 0) {
                    return false;
                }
            }
        }

        StringUtils.StringDiff diff = new StringUtils.StringDiff(normalizedSongTitle,
                captureEntry.capture().scannedTitle);
        songCaptureLinkRepository.save(
                new SongCaptureLink(song, captureEntry, diff.getDistance(), diff.getSimilarity()));

        return true;
    }

    @Override
    protected Void callTask() throws Exception {
        if (!captureRepository.isEmpty()) {
            throw new IllegalStateException();
        }

        songCaptureLinkRepository.deleteAll();

        // create a queue that filtered by selectedCategorySet
        Queue<List<Song>> captureQueue = createCategoryQueue();

        CaptureBound titleBound = getTitleBound();

        // run main task
        try (OcrWrapper ocr = songTitleOcrFactory.create()) {
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
                    String scannedTitle = readTitle(ocr, captureImage, titleBound);
                    scannedTitle = songTitleService.normalizeTitle(scannedTitle);

                    // store cache data and image
                    Capture capture = new Capture(scannedTitle, titleBound);
                    CaptureEntry captureEntry = captureRepository.save(capture);

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
