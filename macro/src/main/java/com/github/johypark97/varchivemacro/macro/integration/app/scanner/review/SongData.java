package com.github.johypark97.varchivemacro.macro.integration.app.scanner.review;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import java.util.LinkedList;
import java.util.List;

public class SongData {
    private final List<LinkedCaptureData> linkList = new LinkedList<>();

    public final Song song;

    public SongData(Song song) {
        this.song = song;
    }

    public void addLinkedCaptureData(CaptureData captureData, int distance) {
        linkList.add(new LinkedCaptureData(captureData, distance));
    }

    public List<LinkedCaptureData> getAllLinkedCaptureData() {
        return List.copyOf(linkList);
    }

    public record LinkedCaptureData(CaptureData captureData, int distance) {
    }
}
