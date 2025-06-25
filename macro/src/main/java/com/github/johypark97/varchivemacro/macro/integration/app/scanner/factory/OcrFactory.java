package com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory;

import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;

public interface OcrFactory {
    OcrWrapper create() throws OcrInitializationError;
}
