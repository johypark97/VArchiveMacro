package com.github.johypark97.varchivemacro.macro.fxgui.model.ocr;

import com.github.johypark97.varchivemacro.lib.scanner.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import java.nio.file.Path;

public class ScannerOcr {
    private static final Path DATAPATH = Path.of("data/tessdata");
    private static final String LANGUAGE = "eng";

    public static OcrWrapper load() throws OcrInitializationError {
        return DefaultOcrWrapper.load(DATAPATH, LANGUAGE);
    }
}
