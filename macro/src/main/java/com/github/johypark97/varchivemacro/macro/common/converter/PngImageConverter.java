package com.github.johypark97.varchivemacro.macro.common.converter;

import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.model.PngImage;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PngImageConverter {
    public static PngImage from(BufferedImage bufferedImage) throws IOException {
        return new PngImage(ImageConverter.imageToPngBytes(bufferedImage));
    }

    public static BufferedImage toBufferedImage(PngImage pngImage) throws IOException {
        return ImageConverter.pngBytesToImage(pngImage.data());
    }
}
