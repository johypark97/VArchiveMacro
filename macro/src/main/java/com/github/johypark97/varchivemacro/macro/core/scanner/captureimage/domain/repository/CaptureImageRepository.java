package com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface CaptureImageRepository {
    boolean isEmpty() throws IOException;

    void deleteAll() throws IOException;

    void save(int id, BufferedImage image) throws IOException;

    BufferedImage findById(int id) throws IOException;
}
