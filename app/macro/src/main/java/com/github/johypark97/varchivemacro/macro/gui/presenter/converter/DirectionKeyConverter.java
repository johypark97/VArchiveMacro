package com.github.johypark97.varchivemacro.macro.gui.presenter.converter;

import com.github.johypark97.varchivemacro.macro.gui.model.datastruct.SettingsData;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro;

public class DirectionKeyConverter {
    public static IMacro.View.DirectionKey data2view(SettingsData.DirectionKey key) {
        if (key == null) {
            throw new NullPointerException();
        }

        return switch (key) {
            case DOWN -> IMacro.View.DirectionKey.DOWN;
            case UP -> IMacro.View.DirectionKey.UP;
        };
    }

    public static SettingsData.DirectionKey view2data(IMacro.View.DirectionKey key) {
        if (key == null) {
            throw new NullPointerException();
        }

        return switch (key) {
            case DOWN -> SettingsData.DirectionKey.DOWN;
            case UP -> SettingsData.DirectionKey.UP;
        };
    }
}
