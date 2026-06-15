package com.github.johypark97.varchivemacro.macro.core.scanner.ocr.infra.service;

import org.bytedeco.leptonica.PIX;

public interface OcrWrapper extends AutoCloseable {
    void setWhitelist(String whitelist);

    String run(PIX pix);

    @Override
    void close();
}
