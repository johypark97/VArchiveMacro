package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface ImageCacheManager {
    String getFormat();

    Path createPath(int number);

    void saveImage(int number, BufferedImage image) throws IOException;

    BufferedImage loadImage(int number) throws IOException;
}
