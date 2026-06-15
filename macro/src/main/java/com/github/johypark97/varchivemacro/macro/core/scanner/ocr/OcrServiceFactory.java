package com.github.johypark97.varchivemacro.macro.core.scanner.ocr;

public class OcrServiceFactory {
    private final String directory;
    private final String language;

    public OcrServiceFactory(String directory, String language) {
        this.directory = directory;
        this.language = language;
    }

    public OcrService create() throws OcrInitializationError {
        return new DefaultOcrService(directory, language);
    }
}
