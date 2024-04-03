package com.github.johypark97.varchivemacro.macro.fxgui.model.manager;

import com.github.johypark97.varchivemacro.lib.scanner.StringUtils.StringDiff;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanDataManager {
    private final List<CaptureData> captureDataList = new ArrayList<>();
    private final List<SongData> songDataList = new ArrayList<>();

    public void clear() {
        captureDataList.clear();
        songDataList.clear();
    }

    public SongData createSongData(int songId, String normalizedTitle) {
        int id = songDataList.size();

        SongData data = new SongData(id, songId, normalizedTitle);
        songDataList.add(data);

        return data;
    }

    public CaptureData createCaptureData() {
        int id = captureDataList.size();

        CaptureData data = new CaptureData(id);
        captureDataList.add(data);

        return data;
    }

    public static class SongData {
        private final List<CaptureData> childList = new ArrayList<>();
        private final Map<CaptureData, LinkMetadata> linkMap = new HashMap<>();

        public final String normalizedTitle;
        public final int id;
        public final int songId;

        public SongData(int id, int songId, String normalizedTitle) {
            this.id = id;
            this.normalizedTitle = normalizedTitle;
            this.songId = songId;
        }

        public List<CaptureData> getChildList() {
            return List.copyOf(childList);
        }

        public Map<CaptureData, LinkMetadata> getLinkMap() {
            return Map.copyOf(linkMap);
        }

        public void link(CaptureData child) {
            if (childList.contains(child)) {
                return;
            }

            childList.add(child);
            linkMap.put(child, new LinkMetadata(this, child));
            child.onLink(this);
        }

        public void unlink(CaptureData child) {
            if (!childList.contains(child)) {
                return;
            }

            childList.remove(child);
            linkMap.remove(child);
            child.onUnlink(this);
        }

        public StringDiff diff(CaptureData captureData) {
            return new StringDiff(normalizedTitle, captureData.scannedTitle);
        }

        @Override
        public String toString() {
            return String.format("SongData{%d, %d '%s'}", id, songId, normalizedTitle);
        }
    }


    public static class CaptureData {
        private final List<SongData> parentList = new ArrayList<>();

        public final int id;

        public Exception exception;
        public String scannedTitle;

        public CaptureData(int id) {
            this.id = id;
        }

        public List<SongData> getParentList() {
            return List.copyOf(parentList);
        }

        private void onLink(SongData parent) {
            parentList.add(parent);
        }

        private void onUnlink(SongData parent) {
            parentList.remove(parent);
        }

        @Override
        public String toString() {
            return String.format("CaptureData{%d, '%s'}", id, scannedTitle);
        }
    }


    public static class LinkMetadata {
        public final double accuracy;
        public final int distance;

        public LinkMetadata(SongData parent, CaptureData child) {
            StringDiff diff = parent.diff(child);

            accuracy = diff.getSimilarity();
            distance = diff.getDistance();
        }

        @Override
        public String toString() {
            return String.format("LinkMetadata{%d, %.2f%%}", distance, accuracy * 100);
        }
    }
}
