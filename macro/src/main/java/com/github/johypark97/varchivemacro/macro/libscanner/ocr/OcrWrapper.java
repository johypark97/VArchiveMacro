package com.github.johypark97.varchivemacro.macro.libscanner.ocr;

import org.bytedeco.leptonica.PIX;

public interface OcrWrapper extends AutoCloseable {
    void setWhitelist(String whitelist);

    String run(PIX pix);

    @Override
    void close();
}
