package com.github.johypark97.varchivemacro.gui.presenter.converter;

import com.github.johypark97.varchivemacro.gui.model.datastruct.SettingsData;
import com.github.johypark97.varchivemacro.gui.presenter.IMacro;

public class DirectionKeyConverter {
    public static IMacro.View.DirectionKey data2view(SettingsData.DirectionKey key) {
        if (key == null)
            throw new NullPointerException();

        return switch (key) {
            case DOWN -> IMacro.View.DirectionKey.DOWN;
            case UP -> IMacro.View.DirectionKey.UP;
            default -> throw new RuntimeException("unknown key");
        };
    }

    public static SettingsData.DirectionKey view2data(IMacro.View.DirectionKey key) {
        if (key == null)
            throw new NullPointerException();

        return switch (key) {
            case DOWN -> SettingsData.DirectionKey.DOWN;
            case UP -> SettingsData.DirectionKey.UP;
            default -> throw new RuntimeException("unknown key");
        };
    }
}
