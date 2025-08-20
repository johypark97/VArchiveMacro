package com.github.johypark97.varchivemacro.macro.integration.app.scanner.review;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import java.util.LinkedList;
import java.util.List;

public class SongData {
    private final List<LinkedCaptureData> linkedCaptureDataList = new LinkedList<>();

    private final Song song;

    private LinkStatus linkStatus = LinkStatus.NOT_DETECTED;

    public SongData(Song song) {
        this.song = song;
    }

    public Song getSong() {
        return song;
    }

    public LinkStatus getLinkStatus() {
        return linkStatus;
    }

    public void setLinkStatus(LinkStatus value) {
        linkStatus = value;
    }

    public List<LinkedCaptureData> getLinkedCaptureDataList() {
        return List.copyOf(linkedCaptureDataList);
    }

    public void setLinkedCaptureDataList(List<LinkedCaptureData> value) {
        linkedCaptureDataList.clear();
        linkedCaptureDataList.addAll(value);
    }
}
