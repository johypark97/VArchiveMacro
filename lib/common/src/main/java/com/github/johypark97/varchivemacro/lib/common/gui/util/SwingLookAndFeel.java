package com.github.johypark97.varchivemacro.lib.common.gui.util;

import javax.swing.UIManager;

public class SwingLookAndFeel {
    public static boolean setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
