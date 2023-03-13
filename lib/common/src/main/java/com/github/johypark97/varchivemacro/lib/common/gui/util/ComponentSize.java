package com.github.johypark97.varchivemacro.lib.common.gui.util;

import java.awt.Component;
import java.awt.Dimension;

public class ComponentSize {
    public static void expandWidthOnly(Component comp) {
        int height = comp.getPreferredSize().height;
        comp.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
    }

    public static void fixToPreferredSize(Component comp) {
        preventExpand(comp);
        preventShrink(comp);
    }

    public static void preventExpand(Component comp) {
        comp.setMaximumSize(comp.getPreferredSize());
    }

    public static void preventShrink(Component comp) {
        comp.setMinimumSize(comp.getPreferredSize());
    }

    public static void setMinimumHeight(Component comp, int height) {
        int width = comp.getMinimumSize().width;
        comp.setMinimumSize(new Dimension(width, height));
    }

    public static void setPreferredWidth(Component comp, int width) {
        int height = comp.getPreferredSize().height;
        comp.setPreferredSize(new Dimension(width, height));
    }

    public static void setPreferredHeight(Component comp, int height) {
        int width = comp.getPreferredSize().width;
        comp.setPreferredSize(new Dimension(width, height));
    }

    public static void setSize(Component comp, Dimension size) {
        comp.setPreferredSize(size);
        fixToPreferredSize(comp);
    }

    public static void shrinkHeightToContents(Component comp) {
        int width = comp.getMaximumSize().width;
        comp.setMaximumSize(new Dimension(width, 0));
    }

    public static void shrinkWidthToContents(Component comp) {
        int height = comp.getPreferredSize().height;
        comp.setMaximumSize(new Dimension(0, height));
    }
}
