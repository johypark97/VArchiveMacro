package com.github.johypark97.varchivemacro.lib.common.gui.component;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JPanel;

public class GrowBoxCreator {
    private final Box box;
    private final Position position;

    public GrowBoxCreator() {
        this(Position.UP);
    }

    public GrowBoxCreator(Position position) {
        this.position = position;

        box = switch (position) {
            case DOWN, UP -> Box.createVerticalBox();
            case LEFT, RIGHT -> Box.createHorizontalBox();
        };
    }

    public Component add(Component component) {
        return box.add(component);
    }

    public JPanel create() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(box, position.getPosition());

        return panel;
    }

    public enum Position {
        DOWN(BorderLayout.PAGE_END), LEFT(BorderLayout.LINE_START), RIGHT(
                BorderLayout.LINE_END), UP(BorderLayout.PAGE_START);

        private final String position;

        Position(String s) {
            position = s;
        }

        public String getPosition() {
            return position;
        }
    }
}
