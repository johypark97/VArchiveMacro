package com.github.johypark97.varchivemacro.gui.model.datastruct;

import com.google.gson.annotations.Expose;

public class SettingsData {
    public enum AnalyzeKey {
        ALT_F11, ALT_F12, ALT_HOME, ALT_INS
    }

    public enum DirectionKey {
        DOWN, UP
    }

    public @Expose AnalyzeKey analyzeKey = SettingsData.AnalyzeKey.ALT_F11;
    public @Expose DirectionKey directionKey = SettingsData.DirectionKey.DOWN;
    public @Expose int captureDuration = 20;
    public @Expose int count = 100;
    public @Expose int inputDuration = 20;
    public @Expose int movingDelay = 500;
}
