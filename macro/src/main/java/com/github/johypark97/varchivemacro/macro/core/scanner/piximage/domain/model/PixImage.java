package com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureBound;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;

public interface PixImage extends AutoCloseable {
    PixImage crop(CaptureBound bound) throws PixImageException;

    float getGrayRatio(int factor, int threshold) throws PixImageException;

    @Override
    void close();
}
