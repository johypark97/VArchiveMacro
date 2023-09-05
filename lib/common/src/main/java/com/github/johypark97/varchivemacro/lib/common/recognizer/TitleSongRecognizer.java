package com.github.johypark97.varchivemacro.lib.common.recognizer;

import com.github.johypark97.varchivemacro.lib.common.StringUtils.StringDiff;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class TitleSongRecognizer {
    private final Function<String, String> titleNormalizer;
    private final Map<String, LocalSong> songMap = new HashMap<>();
    private final TitleTool titleTool;

    public TitleSongRecognizer(TitleTool titleTool) {
        this(titleTool, TitleTool::normalizeTitle_recognition);
    }

    public TitleSongRecognizer(TitleTool titleTool, Function<String, String> titleNormalizer) {
        this.titleNormalizer = titleNormalizer;
        this.titleTool = titleTool;
    }

    public void setSongList(List<LocalSong> songList) {
        Map<String, List<LocalSong>> map = new HashMap<>();
        songList.forEach((song) -> {
            String title =
                    titleTool.hasShortTitle(song) ? titleTool.getShortTitle(song) : song.title();
            title = titleNormalizer.apply(title);

            List<LocalSong> list = map.computeIfAbsent(title, (x) -> new LinkedList<>());
            list.add(song);
        });

        songMap.clear();
        for (Entry<String, List<LocalSong>> entry : map.entrySet()) {
            List<LocalSong> list = entry.getValue();
            LocalSong value = (hasOneElement(list)) ? list.get(0) : null;
            songMap.put(entry.getKey(), value);
        }
    }

    public Recognized recognize(String value) {
        String normalizedValue = titleNormalizer.apply(value);
        if (songMap.containsKey(normalizedValue)) {
            LocalSong song = songMap.get(normalizedValue);

            return new Recognized(
                    (song != null) ? RecognizedStatus.FOUND : RecognizedStatus.DUPLICATED_SONG,
                    song, normalizedValue, normalizedValue, 0, 1);
        }

        List<Entry<String, StringDiff>> candidateList = new ArrayList<>();

        double maximumSimilarity = -1;
        for (String key : songMap.keySet()) {
            StringDiff diff = new StringDiff(normalizedValue, key);
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

            LocalSong song = songMap.get(key);
            return new Recognized(
                    (song != null) ? RecognizedStatus.FOUND : RecognizedStatus.DUPLICATED_SONG,
                    song, normalizedValue, key, diff.getDistance(), (float) diff.getSimilarity());
        }

        return new Recognized(RecognizedStatus.NOT_FOUND, null, normalizedValue, "", 0, 0);
    }

    private boolean hasOneElement(List<?> list) {
        return list.size() == 1;
    }

    public enum RecognizedStatus {
        DUPLICATED_SONG, FOUND, NOT_FOUND
    }


    public record Recognized(RecognizedStatus status, LocalSong song, String normalizedInput,
                             String foundKey, int distance, float similarity) {
    }
}
