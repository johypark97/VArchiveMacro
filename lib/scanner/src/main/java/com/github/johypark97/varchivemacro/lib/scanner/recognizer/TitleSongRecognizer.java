package com.github.johypark97.varchivemacro.lib.scanner.recognizer;

import com.github.johypark97.varchivemacro.lib.scanner.StringUtils.StringDiff;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongManager.LocalSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class TitleSongRecognizer<T extends LocalSong> {
    private final Function<String, String> titleNormalizer;
    private final Map<String, T> songMap = new HashMap<>();
    private final TitleTool titleTool;

    public TitleSongRecognizer(TitleTool titleTool) {
        this(titleTool, TitleTool::normalizeTitle_recognition);
    }

    public TitleSongRecognizer(TitleTool titleTool, Function<String, String> titleNormalizer) {
        this.titleNormalizer = titleNormalizer;
        this.titleTool = titleTool;
    }

    public void setSongList(List<T> songList) {
        Map<String, List<T>> map = new HashMap<>();
        songList.forEach((song) -> {
            String title =
                    titleTool.hasClippedTitle(song) ? titleTool.getClippedTitle(song) : song.title;
            title = titleNormalizer.apply(title);

            List<T> list = map.computeIfAbsent(title, (x) -> new LinkedList<>());
            list.add(song);
        });

        songMap.clear();
        for (Entry<String, List<T>> entry : map.entrySet()) {
            List<T> list = entry.getValue();
            T value = (hasOneElement(list)) ? list.get(0) : null;
            songMap.put(entry.getKey(), value);
        }
    }

    public Recognized<T> recognize(String value) {
        String normalizedValue = titleNormalizer.apply(value);
        String remappedValue = titleTool.remapScannedTitle(normalizedValue);

        if (songMap.containsKey(remappedValue)) {
            T song = songMap.get(remappedValue);

            return new Recognized<>(
                    (song != null) ? RecognizedStatus.FOUND : RecognizedStatus.DUPLICATED_SONG,
                    song, remappedValue, remappedValue, 0, 1);
        }

        List<Entry<String, StringDiff>> candidateList = new ArrayList<>();

        double maximumSimilarity = -1;
        for (String key : songMap.keySet()) {
            StringDiff diff = new StringDiff(remappedValue, key);
            if (diff.getSimilarity() >= maximumSimilarity) {
                if (diff.getSimilarity() > maximumSimilarity) {
                    maximumSimilarity = diff.getSimilarity();
                    candidateList.clear();
                }
                candidateList.add(Map.entry(key, diff));
            }
        }

        if (hasOneElement(candidateList)) {
            Entry<String, StringDiff> entry = candidateList.get(0);

            String key = entry.getKey();
            StringDiff diff = entry.getValue();

            T song = songMap.get(key);
            return new Recognized<>(
                    (song != null) ? RecognizedStatus.FOUND : RecognizedStatus.DUPLICATED_SONG,
                    song, remappedValue, key, diff.getDistance(), (float) diff.getSimilarity());
        }

        return new Recognized<>(RecognizedStatus.NOT_FOUND, null, remappedValue, "", 0, 0);
    }

    private boolean hasOneElement(List<?> list) {
        return list.size() == 1;
    }

    public enum RecognizedStatus {
        DUPLICATED_SONG, FOUND, NOT_FOUND
    }


    public record Recognized<T>(RecognizedStatus status, T song, String normalizedInput,
                                String foundKey, int distance, float similarity) {
    }
}
