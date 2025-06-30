package com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app;

import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;

public interface OcrService extends AutoCloseable {
    String run(PixWrapper pixWrapper);

    @Override
    void close();
}
