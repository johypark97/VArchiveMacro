package com.github.johypark97.varchivemacro.lib.scanner.recognizer;

import com.github.johypark97.varchivemacro.lib.scanner.StringUtils.StringDiff;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized.Found;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized.StatusAccuracy;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized.StatusFound;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class TitleSongRecognizer {
    // normalized title - song lookup
    private final Map<String, List<Song>> lookupSong = new HashMap<>();

    private final Function<String, String> titleNormalizer;
    private final TitleTool titleTool;

    public TitleSongRecognizer(TitleTool titleTool) {
        this(titleTool, TitleTool::normalizeTitle_recognition);
    }

    public TitleSongRecognizer(TitleTool titleTool, Function<String, String> titleNormalizer) {
        this.titleNormalizer = titleNormalizer;
        this.titleTool = titleTool;
    }

    public void setSongList(List<Song> songDataList) {
        lookupSong.clear();

        for (Song song : songDataList) {
            String normalizedTitle = titleTool.getClippedTitleOrDefault(song.id(), song.title());
            normalizedTitle = titleNormalizer.apply(normalizedTitle);

            lookupSong.computeIfAbsent(normalizedTitle, x -> new LinkedList<>()).add(song);
        }
    }

    public Recognized recognize(String title) {
        String normalizedTitle = titleNormalizer.apply(title);
        String remappedTitle = titleTool.remapScannedTitle(normalizedTitle);

        if (!title.isBlank()) {
            List<Found> list;

            // check if there are exact matches
            list = findExactMatch(remappedTitle);
            if (!list.isEmpty()) {
                return new Recognized(remappedTitle, list, StatusAccuracy.FOUND_EXACT,
                        list.size() == 1
                                ? StatusFound.FOUND_ONE_SONG
                                : StatusFound.FOUND_DUPLICATE_SONGS);
            }

            // check if there are similar matches
            list = findSimilarMatch(remappedTitle);
            if (list.size() == 1) {
                return new Recognized(remappedTitle, list, StatusAccuracy.FOUND_SIMILAR,
                        StatusFound.FOUND_ONE_SONG);
            } else if (list.size() > 1) {
                Set<String> set = new HashSet<>();
                for (Found found : list) {
                    set.add(found.key);
                }

                return new Recognized(remappedTitle, list, StatusAccuracy.FOUND_SIMILAR,
                        set.size() == 1
                                ? StatusFound.FOUND_DUPLICATE_SONGS
                                : StatusFound.FOUND_MANY_SONGS);
            }
        }

        return new Recognized(remappedTitle, List.of(), StatusAccuracy.NOT_FOUND,
                StatusFound.NOT_FOUND);
    }

    private List<Found> findExactMatch(String title) {
        if (!lookupSong.containsKey(title)) {
            return List.of();
        }

        List<Found> list = new LinkedList<>();
        for (Song song : lookupSong.get(title)) {
            list.add(new Found(title, song, 0, 1));
        }

        return list;
    }

    private List<Found> findSimilarMatch(String title) {
        Map<String, StringDiff> map = new HashMap<>();

        double maximumSimilarity = -1;
        for (String key : lookupSong.keySet()) {
            StringDiff diff = new StringDiff(title, key);

            if (diff.getSimilarity() >= maximumSimilarity) {
                if (diff.getSimilarity() > maximumSimilarity) {
                    maximumSimilarity = diff.getSimilarity();
                    map.clear();
                }
                map.put(key, diff);
            }
        }

        List<Found> list = new LinkedList<>();
        map.forEach((key, diff) -> {
            for (Song song : lookupSong.get(key)) {
                list.add(new Found(key, song, diff.getDistance(), diff.getSimilarity()));
            }
        });

        return list;
    }


    public record Recognized(String normalizedInput, List<Found> foundList,
                             StatusAccuracy statusAccuracy, StatusFound statusFound) {
        public Set<String> foundKeySet() {
            Set<String> set = new HashSet<>();

            for (Found found : foundList) {
                set.add(found.key);
            }

            return set;
        }

        public enum StatusAccuracy {
            FOUND_EXACT,
            FOUND_SIMILAR,
            NOT_FOUND
        }


        public enum StatusFound {
            FOUND_DUPLICATE_SONGS,
            FOUND_MANY_SONGS,
            FOUND_ONE_SONG,
            NOT_FOUND,
        }


        public record Found(String key, Song song, int distance, double similarity) {
        }
    }
}
