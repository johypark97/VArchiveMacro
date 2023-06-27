package com.github.johypark97.varchivemacro.lib.common.gui.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class RadioButtonGroup<T> {
    private final ButtonGroup group = new ButtonGroup();
    private final Map<T, JRadioButton> buttons = new HashMap<>();

    public JRadioButton getButton(T key) {
        return buttons.get(key);
    }

    public JRadioButton addButton(T key) {
        if (buttons.containsKey(key)) {
            return null;
        }

        JRadioButton button = new JRadioButton();
        buttons.put(key, button);
        group.add(button);

        return button;
    }

    public T getSelected() {
        for (Entry<T, JRadioButton> entry : buttons.entrySet()) {
            if (entry.getValue().isSelected()) {
                return entry.getKey();
            }
        }

        return null;
    }

    public void setSelected(T key) {
        JRadioButton button = buttons.get(key);
        if (button != null) {
            button.setSelected(true);
        }
    }
}
