package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized.Found;
import java.util.List;

public class OcrTestData {
    public final LocalDlcSong targetSong;
    public final String targetNormalizedTitle;

    private List<FoundData> foundDataList;
    private List<String> foundKeyList;
    private Status testStatus = Status.NOT_FOUND;
    private String scannedTitle = "";
    private boolean testPass;

    public OcrTestData(LocalDlcSong targetSong) {
        this.targetSong = targetSong;

        targetNormalizedTitle = TitleTool.normalizeTitle_recognition(targetSong.title);
    }

    public int getTargetId() {
        return targetSong.id;
    }

    public String getTargetTitle() {
        return targetSong.title;
    }

    public String getTargetComposer() {
        return targetSong.composer;
    }

    public String getTargetNormalizedTitle() {
        return targetNormalizedTitle;
    }

    public String getScannedTitle() {
        return scannedTitle;
    }

    public void setScannedTitle(String value) {
        scannedTitle = value;
    }

    public List<String> getFoundKeyList() {
        return List.copyOf(foundKeyList);
    }

    public void setFoundKeyList(List<String> value) {
        foundKeyList = List.copyOf(value);
    }

    public List<FoundData> getFoundDataList() {
        return List.copyOf(foundDataList);
    }

    public void setFoundDataList(List<FoundData> value) {
        foundDataList = List.copyOf(value);
    }

    public Status getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(Status value) {
        testStatus = value;
    }

    public boolean isTestPass() {
        return testPass;
    }

    public void setTestPass(boolean value) {
        testPass = value;
    }

    public enum Status {
        DUPLICATED_EXACT,
        DUPLICATED_SIMILAR,
        EXACT,
        NOT_FOUND,
        SIMILAR,
        WRONG
    }


    public static class FoundData {
        public final LocalDlcSong song;
        public final double accuracy;
        public final int distance;

        public FoundData(Found<LocalDlcSong> found) {
            accuracy = found.similarity();
            distance = found.distance();
            song = found.song();
        }
    }
}
