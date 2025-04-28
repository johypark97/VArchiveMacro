package com.github.johypark97.varchivemacro.macro.infrastructure.ocr;

import com.github.johypark97.varchivemacro.lib.scanner.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrInitializationError;
import java.nio.file.Path;

public class ScannerOcr extends DefaultOcrWrapper {
    private static final Path DATAPATH = Path.of("data");
    private static final String LANGUAGE = "eng";

    public ScannerOcr() throws OcrInitializationError {
        super(DATAPATH, LANGUAGE);
    }
}
