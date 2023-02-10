package com.github.johypark97.varchivemacro.lib.common.gui.component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.JCheckBox;

public class CheckboxGroup<T> {
    private final Map<T, JCheckBox> checkboxes = new LinkedHashMap<>();

    public void clear() {
        checkboxes.clear();
    }

    public void add(T key, String text) {
        checkboxes.put(key, new JCheckBox(text));
    }

    public JCheckBox getCheckbox(T key) {
        return checkboxes.get(key);
    }

    public Set<T> getSelected() {
        return checkboxes.entrySet().stream().filter((entry) -> entry.getValue().isSelected())
                .map(Entry::getKey).collect(Collectors.toSet());
    }

    public void select(Set<T> keys) {
        applyToAll(keys::contains, true);
    }

    public void unselect(Set<T> keys) {
        applyToAll(keys::contains, false);
    }

    public void selectAll() {
        applyToAll((key) -> true, true);
    }

    public void unselectAll() {
        applyToAll((key) -> true, false);
    }

    public void selectAllExclude(Set<T> excludeKeys) {
        applyToAll((key) -> !excludeKeys.contains(key), true);
    }

    public void unselectAllExclude(Set<T> excludeKeys) {
        applyToAll((key) -> !excludeKeys.contains(key), false);
    }

    public void forEach(BiConsumer<T, JCheckBox> action) {
        checkboxes.forEach(action);
    }

    protected void applyToAll(Predicate<T> keyFilter, boolean select) {
        checkboxes.entrySet().stream().filter((entry) -> keyFilter.test(entry.getKey()))
                .forEach((entry) -> entry.getValue().setSelected(select));
    }
}
