package com.github.johypark97.varchivemacro.gui.view.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class RadioButtonGroup<T> extends JPanel {
    private ButtonGroup group = new ButtonGroup();
    private Map<T, JRadioButton> buttons = new HashMap<>();

    public JRadioButton getButton(T key) {
        return buttons.get(key);
    }

    public boolean addButton(T key) {
        if (buttons.containsKey(key))
            return false;

        JRadioButton button = new JRadioButton();
        buttons.put(key, button);
        group.add(button);

        add(button);
        return true;
    }

    public boolean setSelected(T key) {
        JRadioButton button = buttons.get(key);
        if (button == null)
            return false;

        button.setSelected(true);
        return true;
    }

    public T getSelected() {
        Iterator<Entry<T, JRadioButton>> iter = buttons.entrySet().iterator();

        while (iter.hasNext()) {
            Entry<T, JRadioButton> entry = iter.next();
            if (entry.getValue().isSelected())
                return entry.getKey();
        }

        return null;
    }
}
