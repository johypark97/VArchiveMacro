package com.github.johypark97.varchivemacro.lib.scanner.recognizer;

import static com.github.johypark97.varchivemacro.lib.common.CollectionUtility.hasMany;
import static com.github.johypark97.varchivemacro.lib.common.CollectionUtility.hasOne;

import com.github.johypark97.varchivemacro.lib.scanner.StringUtils.StringDiff;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongManager.LocalSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized.Found;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized.StatusAccuracy;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized.StatusFound;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

public class TitleSongRecognizer<T extends LocalSong> {
    // normalized title - song lookup
    private final Map<String, List<T>> lookupSong = new HashMap<>();

    private final Function<String, String> titleNormalizer;
    private final TitleTool titleTool;

    public TitleSongRecognizer(TitleTool titleTool) {
        this(titleTool, TitleTool::normalizeTitle_recognition);
    }

    public TitleSongRecognizer(TitleTool titleTool, Function<String, String> titleNormalizer) {
        this.titleNormalizer = titleNormalizer;
        this.titleTool = titleTool;
    }

    public void setSongList(List<T> songList) {
        lookupSong.clear();

        for (T song : songList) {
            String normalizedTitle = titleTool.getClippedTitleOrDefault(song.id, song.title);
            normalizedTitle = titleNormalizer.apply(normalizedTitle);

            lookupSong.computeIfAbsent(normalizedTitle, x -> new LinkedList<>()).add(song);
        }
    }

    public Recognized<T> recognize(String title) {
        String normalizedTitle = titleNormalizer.apply(title);
        String remappedTitle = titleTool.remapScannedTitle(normalizedTitle);

        if (!title.isBlank()) {
            List<Found<T>> list;

            // check if there are exact matches
            list = findExactMatch(remappedTitle);
            if (!list.isEmpty()) {
                return new Recognized<>(remappedTitle, list, StatusAccuracy.FOUND_EXACT,
                        hasOne(list)
                                ? StatusFound.FOUND_ONE_SONG
                                : StatusFound.FOUND_DUPLICATE_SONGS);
            }

            // check if there are similar matches
            list = findSimilarMatch(remappedTitle);
            if (hasOne(list)) {
                return new Recognized<>(remappedTitle, list, StatusAccuracy.FOUND_SIMILAR,
                        StatusFound.FOUND_ONE_SONG);
            } else if (hasMany(list)) {
                Set<String> set = new HashSet<>();
                for (Found<T> found : list) {
                    set.add(found.key);
                }

                return new Recognized<>(remappedTitle, list, StatusAccuracy.FOUND_SIMILAR,
                        hasOne(set)
                                ? StatusFound.FOUND_DUPLICATE_SONGS
                                : StatusFound.FOUND_MANY_SONGS);
            }
        }

        return new Recognized<>(remappedTitle, List.of(), StatusAccuracy.NOT_FOUND,
                StatusFound.NOT_FOUND);
    }

    private List<Found<T>> findExactMatch(String title) {
        if (!lookupSong.containsKey(title)) {
            return List.of();
        }

        List<Found<T>> list = new LinkedList<>();
        for (T song : lookupSong.get(title)) {
            list.add(new Found<>(title, song, 0, 1));
        }

        return list;
    }

    private List<Found<T>> findSimilarMatch(String title) {
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

        List<Found<T>> list = new LinkedList<>();
        for (Entry<String, StringDiff> entry : map.entrySet()) {
            String key = entry.getKey();
            StringDiff diff = entry.getValue();

            for (T song : lookupSong.get(key)) {
                list.add(new Found<>(key, song, diff.getDistance(), diff.getSimilarity()));
            }
        }

        return list;
    }


    public static class Recognized<T extends LocalSong> {
        public final List<Found<T>> foundList;

        public final StatusAccuracy statusAccuracy;
        public final StatusFound statusFound;

        public final String normalizedInput;

        public Recognized(String normalizedInput, List<Found<T>> foundList,
                StatusAccuracy statusAccuracy, StatusFound statusFound) {
            this.foundList = foundList;
            this.normalizedInput = normalizedInput;
            this.statusAccuracy = statusAccuracy;
            this.statusFound = statusFound;
        }

        public Set<String> foundKeySet() {
            Set<String> set = new HashSet<>();

            for (Found<T> found : foundList) {
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


        public record Found<T extends LocalSong>(String key, T song, int distance,
                                                 double similarity) {
        }
    }
}
