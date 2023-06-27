package com.github.johypark97.varchivemacro.lib.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

// TODO: ImageIO is slow. Need to replace to another.
public class ImageConverter {
    private static final String PNG_FORMAT = "png";

    public static void disableDiskCache() {
        ImageIO.setUseCache(false);
    }

    public static byte[] imageToPngBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, PNG_FORMAT, stream);
        return stream.toByteArray();
    }

    public static BufferedImage pngBytesToImage(byte[] bytes) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        return ImageIO.read(stream);
    }
}
