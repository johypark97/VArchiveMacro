package com.github.johypark97.varchivemacro.lib.jfx.fxgui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

public class SliderTextFieldLinker {
    private final IntegerProperty linkedValue = new SimpleIntegerProperty();

    public final Slider slider;
    public final TextField textField;

    private Integer defaultValue;
    private Integer limitMax;
    private Integer limitMin;
    private boolean blockSliderEvent;

    public SliderTextFieldLinker(Slider slider, TextField textField) {
        this.slider = slider;
        this.textField = textField;

        setHandler();
    }

    public final ReadOnlyIntegerProperty valueProperty() {
        return linkedValue;
    }

    public void setDefaultValue(int value) {
        defaultValue = value;
    }

    public void clearDefaultValue() {
        defaultValue = null; // NOPMD
    }

    public void setLimitMax(int value) {
        limitMax = value;
    }

    public void clearLimitMax() {
        limitMax = null; // NOPMD
    }

    public void setLimitMin(int value) {
        limitMin = value;
    }

    public void clearLimitMin() {
        limitMin = null; // NOPMD
    }

    public int getValue() {
        return linkedValue.get();
    }

    public void setValue(int value) {
        setValuePropertyValue(value);

        updateSliderValue();
        updateTextFieldText();
    }

    private void setHandler() {
        // onMouseClicked
        slider.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.SECONDARY && defaultValue != null) {
                setValue(defaultValue);
            }
        });

        // onValueChanged
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!blockSliderEvent) {
                setValue(Math.round(newValue.floatValue()));
            }
        });

        // onFocusLost
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                updateFromTextField();
            }
        });

        // onEnter
        textField.setOnAction(event -> updateFromTextField());
    }

    private void setValuePropertyValue(int value) {
        int max = (limitMax != null) ? limitMax : (int) slider.getMax();
        int min = (limitMin != null) ? limitMin : (int) slider.getMin();

        linkedValue.set(Math.min(Math.max(min, value), max));
    }

    private void updateSliderValue() {
        blockSliderEvent = true; // NOPMD
        slider.setValue(linkedValue.get());
        blockSliderEvent = false;
    }

    private void updateTextFieldText() {
        textField.setText(Integer.toString(linkedValue.get()));
    }

    private void updateFromTextField() {
        try {
            String input = textField.getText().trim();
            int value = Integer.parseInt(input);
            setValuePropertyValue(value);
        } catch (NumberFormatException ignored) {
            updateTextFieldText();
            return;
        }

        updateSliderValue();
        updateTextFieldText();
    }
}
