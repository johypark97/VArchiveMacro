package com.github.johypark97.varchivemacro.macro.integration.app.scanner.review;

import com.github.johypark97.varchivemacro.lib.scanner.StringUtils;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.app.SongCaptureLinkService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongCaptureLinkingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SongCaptureLinkingService.class);

    private static final int LINK_DUPLICATE_LIMIT = 2;

    private final CaptureService captureService;
    private final SongCaptureLinkService songCaptureLinkService;
    private final SongService songService;
    private final SongTitleService songTitleService;

    private final Map<Song.Pack.Category, Map<String, List<Song>>> normalizedSongTitleLookup =
            new HashMap<>();

    public SongCaptureLinkingService(CaptureService captureService,
            SongCaptureLinkService songCaptureLinkService, SongService songService,
            SongTitleService songTitleService) {
        this.captureService = captureService;
        this.songCaptureLinkService = songCaptureLinkService;
        this.songService = songService;
        this.songTitleService = songTitleService;
    }

    public void link() {
        if (!songCaptureLinkService.isEmpty()) {
            throw new IllegalStateException();
        }

        captureService.findAll().forEach(captureEntry -> {
            Map<String, List<Song>> lookup =
                    getNormalizedSongTitleLookup(captureEntry.capture().category);

            findAndLinkSongAndCaptureEntry(lookup, captureEntry);
        });
    }

    private Map<String, List<Song>> getNormalizedSongTitleLookup(Song.Pack.Category category) {
        return normalizedSongTitleLookup.computeIfAbsent(category,
                x -> songService.groupSongByCategory().get(x).stream().collect(
                        Collectors.groupingBy(song -> songTitleService.normalizeTitle(
                                songTitleService.getClippedTitleOrDefault(song)))));
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
}
