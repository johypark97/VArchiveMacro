package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.common.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrInitializationError;
import java.nio.file.Path;

public class TitleOcr extends DefaultOcrWrapper {
    private static final Path DATAPATH = Path.of("data/tessdata");
    private static final String LANGUAGE = "djmax";

    public TitleOcr() throws OcrInitializationError {
        super(DATAPATH, LANGUAGE);
    }
}
