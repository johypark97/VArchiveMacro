package com.github.johypark97.varchivemacro.lib.common.gui.component;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderSet {
    protected JLabel label = new JLabel();
    protected JSlider slider = new JSlider();
    protected JTextField textField = new JTextField();

    private boolean hasDefault = false;
    private boolean hasLimitMax = false;
    private boolean hasLimitMin = false;
    private int defaultValue;
    private int limitMax;
    private int limitMin;
    private int value = 0;

    // event listener
    private final Listener listener = new Listener();

    public SliderSet() {
        slider.addChangeListener(listener);
        slider.addMouseListener(listener);
        textField.addFocusListener(listener);
        textField.addKeyListener(listener);
    }

    public JLabel getLabel() {
        return label;
    }

    public JSlider getSlider() {
        return slider;
    }

    public JTextField getTextField() {
        return textField;
    }

    public void setDefault(int value) {
        defaultValue = value;
        hasDefault = true;
    }

    public void setLimitMax(int value) {
        limitMax = value;
        hasLimitMax = true;
    }

    public void setLimitMin(int value) {
        limitMin = value;
        hasLimitMin = true;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        listener.updateValue(value);
    }

    private class Listener implements ChangeListener, FocusListener, KeyListener, MouseListener {
        private boolean blockStateChanged = false;

        public void updateValue(int x) {
            blockStateChanged = true;

            int sliderMax = slider.getMaximum();
            int sliderMin = slider.getMinimum();
            int max = hasLimitMax ? limitMax : sliderMax;
            int min = hasLimitMin ? limitMin : sliderMin;

            if (x < min) {
                value = min;
            } else if (x > max) {
                value = max;
            } else {
                value = x;
            }

            slider.setValue(value);
            slider.setPaintTrack(sliderMin <= value && value <= sliderMax);
            textField.setText(Integer.toString(value));

            SwingUtilities.invokeLater(() -> blockStateChanged = false);
        }

        private void updateValue_textField() {
            try {
                String text = textField.getText().trim();
                int input = Integer.parseInt(text);
                updateValue(input);
            } catch (NumberFormatException e) {
                textField.setText(Integer.toString(value));
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            updateValue_textField();
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                updateValue_textField();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (hasDefault && SwingUtilities.isRightMouseButton(e)) {
                updateValue(defaultValue);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!blockStateChanged) {
                updateValue(slider.getValue());
            }
        }
    }
}
