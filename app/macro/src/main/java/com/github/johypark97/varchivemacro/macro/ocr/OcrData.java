package com.github.johypark97.varchivemacro.macro.ocr;

import java.awt.Rectangle;

public class OcrData {
    public String string;
    public final int height;
    public final int width;
    public final int x;
    public final int y;

    public OcrData(Rectangle rectangle) {
        height = rectangle.height;
        width = rectangle.width;
        x = rectangle.x;
        y = rectangle.y;
    }

    // Temporary method to resolve spotbugs URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD warning.
    @Override
    public String toString() {
        return String.format("[(%d, %d) - (%d, %d)] %s", x, y, x + width, y + height, string);
    }
}
