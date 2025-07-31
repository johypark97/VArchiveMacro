package com.github.johypark97.varchivemacro.macro.core.scanner.piximage.infra.model;

import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DefaultPixImage implements PixImage {
    public final PixWrapper pixWrapper;

    public DefaultPixImage(BufferedImage image) throws IOException, PixImageException {
        try {
            pixWrapper = new PixWrapper(ImageConverter.imageToPngBytes(image));
        } catch (PixError e) {
            throw new PixImageException(e);
        }
    }

    @Override
    public float getGrayRatio(int factor, int threshold) throws PixImageException {
        try {
            return pixWrapper.getGrayRatio(factor, threshold);
        } catch (PixError e) {
            throw new PixImageException(e);
        }
    }

    @Override
    public void close() {
        pixWrapper.close();
    }
}
