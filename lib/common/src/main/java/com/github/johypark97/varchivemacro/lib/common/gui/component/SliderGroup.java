package com.github.johypark97.varchivemacro.lib.common.gui.component;

import com.github.johypark97.varchivemacro.lib.common.gui.util.ComponentSize;
import java.io.Serial;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class SliderGroup<T> extends JPanel {
    @Serial
    private static final long serialVersionUID = 1679127316295053614L;

    private final GroupLayout layout = new GroupLayout(this);
    private final Map<T, SliderSet> sliders = new LinkedHashMap<>();

    public SliderGroup() {
        setLayout(layout);
    }

    public SliderSet addNewSliderSet(T key) {
        if (sliders.containsKey(key)) {
            return null;
        }

        SliderSet slider = new SliderSet();
        sliders.put(key, slider);
        return slider;
    }

    public SliderSet getSliderSet(T key) {
        return sliders.get(key);
    }

    public void setWidth(int value) {
        ComponentSize.setPreferredWidth(this, value);
    }

    public void forEachSliderSets(Consumer<SliderSet> action) {
        sliders.forEach((key, slider) -> action.accept(slider));
    }

    public void forEachLabels(Consumer<JLabel> action) {
        sliders.forEach((key, slider) -> action.accept(slider.label));
    }

    public void forEachSliders(Consumer<JSlider> action) {
        sliders.forEach((key, slider) -> action.accept(slider.slider));
    }

    public void forEachTextFields(Consumer<JTextField> action) {
        sliders.forEach((key, slider) -> action.accept(slider.textField));
    }

    public void setupLayout() {
        setupHGroup();
        setupVGroup();
    }

    private void setupHGroup() {
        SequentialGroup hGroup = layout.createSequentialGroup();

        ParallelGroup subGroupLabel = layout.createParallelGroup(Alignment.TRAILING);
        ParallelGroup subGroupSlider = layout.createParallelGroup();
        ParallelGroup subGroupTextField = layout.createParallelGroup();

        for (SliderSet slider : sliders.values()) {
            subGroupLabel.addComponent(slider.label);
            subGroupSlider.addComponent(slider.slider);
            subGroupTextField.addComponent(slider.textField);
        }

        hGroup.addGroup(subGroupLabel);
        hGroup.addGroup(subGroupSlider);
        hGroup.addGroup(subGroupTextField);

        layout.setHorizontalGroup(hGroup);
    }

    private void setupVGroup() {
        SequentialGroup vGroup = layout.createSequentialGroup();

        for (SliderSet slider : sliders.values()) {
            ParallelGroup subGroup = layout.createParallelGroup(Alignment.CENTER);
            subGroup.addComponent(slider.label);
            subGroup.addComponent(slider.slider);
            subGroup.addComponent(slider.textField);
            vGroup.addGroup(subGroup);
        }

        layout.setVerticalGroup(vGroup);
    }
}
