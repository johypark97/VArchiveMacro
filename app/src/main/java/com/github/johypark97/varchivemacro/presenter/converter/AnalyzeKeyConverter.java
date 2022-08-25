package com.github.johypark97.varchivemacro.presenter.converter;

import com.github.johypark97.varchivemacro.model.datastruct.SettingsData;
import com.github.johypark97.varchivemacro.presenter.IMacro;

public class AnalyzeKeyConverter {
    public static IMacro.View.AnalyzeKey data2view(SettingsData.AnalyzeKey key) {
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

    public static SettingsData.AnalyzeKey view2data(IMacro.View.AnalyzeKey key) {
        if (key == null)
            throw new NullPointerException();

        return switch (key) {
            case ALT_F11 -> SettingsData.AnalyzeKey.ALT_F11;
            case ALT_F12 -> SettingsData.AnalyzeKey.ALT_F12;
            case ALT_HOME -> SettingsData.AnalyzeKey.ALT_HOME;
            case ALT_INS -> SettingsData.AnalyzeKey.ALT_INS;
            default -> throw new RuntimeException("unknown key");
        };
    }
}
