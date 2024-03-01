package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;

public class OcrTestData {
    private final LocalDlcSong targetSong;

    private LocalDlcSong recognizedSong;

    public Status testStatus = Status.NOT_FOUND;
    public String matchFoundKey = "";
    public String matchScannedTitle = "";
    public boolean testPass;
    public double testAccuracy;
    public final String matchNormalizedTitle;
    public int testDistance;

    public OcrTestData(LocalDlcSong targetSong) {
        this.targetSong = targetSong;

        matchNormalizedTitle = TitleTool.normalizeTitle_recognition(targetSong.title);
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

    public String getMatchNormalizedTitle() {
        return matchNormalizedTitle;
    }

    public String getMatchScannedTitle() {
        return matchScannedTitle;
    }

    public void setMatchScannedTitle(String value) {
        matchScannedTitle = value;
    }

    public String getMatchFoundKey() {
        return matchFoundKey;
    }

    public void setMatchFoundKey(String value) {
        matchFoundKey = value;
    }

    public void setRecognizedSong(LocalDlcSong value) {
        recognizedSong = value;
    }

    public int getRecognizedId() {
        return (recognizedSong != null) ? recognizedSong.id : -1;
    }

    public String getRecognizedTitle() {
        return (recognizedSong != null) ? recognizedSong.title : "";
    }

    public String getRecognizedComposer() {
        return (recognizedSong != null) ? recognizedSong.composer : "";
    }

    public int getTestDistance() {
        return testDistance;
    }

    public void setTestDistance(int value) {
        testDistance = value;
    }

    public double getTestAccuracy() {
        return testAccuracy;
    }

    public void setTestAccuracy(double value) {
        testAccuracy = value;
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
        DUPLICATED, EXACT, NOT_FOUND, SIMILAR, WRONG
    }
}
