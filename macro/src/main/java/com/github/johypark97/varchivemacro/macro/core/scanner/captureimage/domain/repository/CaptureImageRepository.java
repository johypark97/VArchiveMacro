package com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.model.PngImage;
import java.io.IOException;

public interface CaptureImageRepository {
    boolean isEmpty() throws IOException;

    void deleteAll() throws IOException;

    void save(int id, PngImage pngImage) throws IOException;

    PngImage findById(int id) throws IOException;
}
