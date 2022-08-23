package com.github.johypark97.varchivemacro.presenter;

import com.github.johypark97.varchivemacro.model.MacroData;

class AnalyzeKeyConverter {
    public static IMacro.View.AnalyzeKey data2view(MacroData.AnalyzeKey key) {
        if (key == null)
            throw new NullPointerException();

        return switch (key) {
            case ALT_F11 -> IMacro.View.AnalyzeKey.ALT_F11;
            case ALT_F12 -> IMacro.View.AnalyzeKey.ALT_F12;
            case ALT_HOME -> IMacro.View.AnalyzeKey.ALT_HOME;
            case ALT_INS -> IMacro.View.AnalyzeKey.ALT_INS;
            default -> throw new RuntimeException("unknown key");
        };
    }

    public static MacroData.AnalyzeKey view2data(IMacro.View.AnalyzeKey key) {
        if (key == null)
            throw new NullPointerException();

        return switch (key) {
            case ALT_F11 -> MacroData.AnalyzeKey.ALT_F11;
            case ALT_F12 -> MacroData.AnalyzeKey.ALT_F12;
            case ALT_HOME -> MacroData.AnalyzeKey.ALT_HOME;
            case ALT_INS -> MacroData.AnalyzeKey.ALT_INS;
            default -> throw new RuntimeException("unknown key");
        };
    }
}


class DirectionKeyConverter {
    public static IMacro.View.DirectionKey data2view(MacroData.DirectionKey key) {
        if (key == null)
            throw new NullPointerException();

        return switch (key) {
            case DOWN -> IMacro.View.DirectionKey.DOWN;
            case UP -> IMacro.View.DirectionKey.UP;
            default -> throw new RuntimeException("unknown key");
        };
    }

    public static MacroData.DirectionKey view2data(IMacro.View.DirectionKey key) {
        if (key == null)
            throw new NullPointerException();

        return switch (key) {
            case DOWN -> MacroData.DirectionKey.DOWN;
            case UP -> MacroData.DirectionKey.UP;
            default -> throw new RuntimeException("unknown key");
        };
    }
}
