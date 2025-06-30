package com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.infra.exception.OcrInitializationException;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.infra.service.DefaultOcrService;
import java.nio.file.Path;

public class OcrServiceFactory {
    private final Path traineddataDirectoryPath;
    private final String language;

    public OcrServiceFactory(Path traineddataDirectoryPath, String language) {
        this.traineddataDirectoryPath = traineddataDirectoryPath;
        this.language = language;
    }

    public OcrService create() throws OcrInitializationException {
        return new DefaultOcrService(traineddataDirectoryPath, language);
    }
}
