package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.ocr;

import com.github.johypark97.varchivemacro.lib.scanner.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrInitializationError;
import java.nio.file.Path;

public class TitleOcr extends DefaultOcrWrapper {
    private static final Path DATAPATH = Path.of("data");
    private static final String LANGUAGE = "djmax";

    public TitleOcr() throws OcrInitializationError {
        super(DATAPATH, LANGUAGE);
    }
}
