package com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.Table.Cell;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

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

    protected BufferedImage crop(BufferedImage image, Rectangle r) {
        return image.getSubimage(r.x, r.y, r.width, r.height);
    }

    @Override
    public ImmutableSet<Cell<Button, Pattern, String>> keys() {
        return KEY_TABLE.cellSet();
    }

    @Override
    public BufferedImage getTitle(BufferedImage image) {
        return crop(image, getTitle());
    }

    @Override
    public BufferedImage getRate(BufferedImage image, Button button, Pattern pattern) {
        return crop(image, getRate(button, pattern));
    }

    @Override
    public BufferedImage getComboMark(BufferedImage image, Button button, Pattern pattern) {
        return crop(image, getComboMark(button, pattern));
    }
}
