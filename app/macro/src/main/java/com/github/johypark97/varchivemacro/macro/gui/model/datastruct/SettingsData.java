package com.github.johypark97.varchivemacro.macro.gui.model.datastruct;

import com.google.gson.annotations.Expose;

public class SettingsData {
    public enum AnalyzeKey {
        ALT_F11, ALT_F12, ALT_HOME, ALT_INS
    }

    public enum DirectionKey {
        DOWN, UP
    }

    @Expose
    public AnalyzeKey analyzeKey = SettingsData.AnalyzeKey.ALT_F11;

    @Expose
    public DirectionKey directionKey = SettingsData.DirectionKey.DOWN;

    @Expose
    public int captureDuration = 20;

    @Expose
    public int count = 100;

    @Expose
    public int inputDuration = 20;

    @Expose
    public int movingDelay = 500;
}
