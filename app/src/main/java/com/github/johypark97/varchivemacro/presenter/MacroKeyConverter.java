package com.github.johypark97.varchivemacro.presenter;

import com.github.johypark97.varchivemacro.model.MacroData;

class AnalyzeKeyConverter {
    public static IMacro.View.AnalyzeKey data2view(MacroData.AnalyzeKey key) {
        if (key == null)
            throw new NullPointerException();

        switch (key) {
            case ALT_F11:
                return IMacro.View.AnalyzeKey.ALT_F11;
            case ALT_F12:
                return IMacro.View.AnalyzeKey.ALT_F12;
            case ALT_HOME:
                return IMacro.View.AnalyzeKey.ALT_HOME;
            case ALT_INS:
                return IMacro.View.AnalyzeKey.ALT_INS;
            default:
                throw new RuntimeException("unknown key");
        }
    }

    public static MacroData.AnalyzeKey view2data(IMacro.View.AnalyzeKey key) {
        if (key == null)
            throw new NullPointerException();

        switch (key) {
            case ALT_F11:
                return MacroData.AnalyzeKey.ALT_F11;
            case ALT_F12:
                return MacroData.AnalyzeKey.ALT_F12;
            case ALT_HOME:
                return MacroData.AnalyzeKey.ALT_HOME;
            case ALT_INS:
                return MacroData.AnalyzeKey.ALT_INS;
            default:
                throw new RuntimeException("unknown key");
        }
    }
}


class DirectionKeyConverter {
    public static IMacro.View.DirectionKey data2view(MacroData.DirectionKey key) {
        if (key == null)
            throw new NullPointerException();

        switch (key) {
            case DOWN:
                return IMacro.View.DirectionKey.DOWN;
            case UP:
                return IMacro.View.DirectionKey.UP;
            default:
                throw new RuntimeException("unknown key");
        }
    }

    public static MacroData.DirectionKey view2data(IMacro.View.DirectionKey key) {
        if (key == null)
            throw new NullPointerException();

        switch (key) {
            case DOWN:
                return MacroData.DirectionKey.DOWN;
            case UP:
                return MacroData.DirectionKey.UP;
            default:
                throw new RuntimeException("unknown key");
        }
    }
}
