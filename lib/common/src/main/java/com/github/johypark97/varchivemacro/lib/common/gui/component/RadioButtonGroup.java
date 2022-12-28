package com.github.johypark97.varchivemacro.lib.common.gui.component;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class RadioButtonGroup<T> extends JPanel {
    @Serial
    private static final long serialVersionUID = -5179533965452833323L;

    private final ButtonGroup group = new ButtonGroup();
    private final Map<T, JRadioButton> buttons = new HashMap<>();

    public JRadioButton getButton(T key) {
        return buttons.get(key);
    }

    public void addButton(T key) {
        if (!buttons.containsKey(key)) {
            JRadioButton button = new JRadioButton();
            buttons.put(key, button);
            group.add(button);
            add(button);
        }
    }

    public void setSelected(T key) {
        JRadioButton button = buttons.get(key);
        if (button != null) {
            button.setSelected(true);
        }
    }

    public T getSelected() {
        for (Entry<T, JRadioButton> entry : buttons.entrySet()) {
            if (entry.getValue().isSelected()) {
                return entry.getKey();
            }
        }

        return null;
    }
}
