package com.github.johypark97.varchivemacro.lib.jfx.fxgui;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

public class SliderTextFieldLinker {
    public final Slider slider;
    public final TextField textField;

    private boolean blockSliderProperty = false;
    private final ReadOnlyIntegerWrapper valueProperty = new ReadOnlyIntegerWrapper();

    private Integer defaultValue;
    private Integer limitMax;
    private Integer limitMin;

    public SliderTextFieldLinker(Slider slider, TextField textField) {
        this.slider = slider;
        this.textField = textField;

        setHandler();
    }

    public ReadOnlyIntegerProperty valueReadOnlyProperty() {
        return valueProperty.getReadOnlyProperty();
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
        return valueProperty.intValue();
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
            if (!blockSliderProperty) {
                setValue(newValue.intValue());
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

        valueProperty.set(Math.min(Math.max(min, value), max));
    }

    private void updateSliderValue() {
        blockSliderProperty = true;
        slider.setValue(valueProperty.intValue());
        blockSliderProperty = false;
    }

    private void updateTextFieldText() {
        textField.setText(Integer.toString(valueProperty.intValue()));
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
