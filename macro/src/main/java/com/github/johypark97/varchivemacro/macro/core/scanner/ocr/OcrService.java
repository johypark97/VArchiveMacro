package com.github.johypark97.varchivemacro.macro.core.scanner.ocr;

import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.model.PixImage;

public interface OcrService extends AutoCloseable {
    String run(PixImage pixImage);

    void setWhitelist(String whitelist);

    @Override
    void close();
}
