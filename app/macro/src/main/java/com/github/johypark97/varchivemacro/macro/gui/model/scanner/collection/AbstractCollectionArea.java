package com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.Table.Cell;

abstract class AbstractCollectionArea implements CollectionArea {
    protected static final ImmutableTable<Button, Pattern, String> KEY_TABLE;

    static {
        Builder<Button, Pattern, String> builder = ImmutableTable.builder();

        for (Button button : Button.values()) {
            for (Pattern pattern : Pattern.values()) {
                // key format: _4B_NM
                String value = String.format("%sB_%s", button, pattern);
                builder.put(button, pattern, value);
            }
        }

        KEY_TABLE = builder.build();
    }

    @Override
    public ImmutableSet<Cell<Button, Pattern, String>> keys() {
        return KEY_TABLE.cellSet();
    }
}
