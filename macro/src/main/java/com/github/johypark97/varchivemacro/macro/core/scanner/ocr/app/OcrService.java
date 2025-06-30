package com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;

public interface OcrService extends AutoCloseable {
    String run(PixImage pixImage);

    @Override
    void close();
}
