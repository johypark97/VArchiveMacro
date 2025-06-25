package com.github.johypark97.varchivemacro.macro.core.scanner.cache.infra;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface CaptureImageCache {
    void validate() throws IOException;

    void prepare() throws IOException;

    void write(int id, BufferedImage image) throws IOException;

    BufferedImage read(int id) throws IOException;
}
