package com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;
import java.awt.Rectangle;

public interface PixImage extends AutoCloseable {
    PixImage crop(Rectangle rectangle) throws PixImageException;

    float getGrayRatio(int factor, int threshold) throws PixImageException;

    @Override
    void close();
}
