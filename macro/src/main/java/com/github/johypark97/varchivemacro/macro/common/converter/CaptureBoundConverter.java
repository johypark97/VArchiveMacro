package com.github.johypark97.varchivemacro.macro.common.converter;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureBound;
import java.awt.Rectangle;

public class CaptureBoundConverter {
    public static CaptureBound fromRectangle(Rectangle rectangle) {
        return new CaptureBound(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public static Rectangle toRectangle(CaptureBound bound) {
        return new Rectangle(bound.x(), bound.y(), bound.width(), bound.height());
    }
}
