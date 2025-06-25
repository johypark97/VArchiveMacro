package com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory;

import com.github.johypark97.varchivemacro.lib.scanner.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import java.nio.file.Path;

public class DefaultOcrFactory implements OcrFactory {
    private final Path dataDirectoryPath;
    private final String language;

    public DefaultOcrFactory(Path dataDirectoryPath, String language) {
        this.dataDirectoryPath = dataDirectoryPath;
        this.language = language;
    }

    @Override
    public OcrWrapper create() throws OcrInitializationError {
        return new DefaultOcrWrapper(dataDirectoryPath, language);
    }
}
