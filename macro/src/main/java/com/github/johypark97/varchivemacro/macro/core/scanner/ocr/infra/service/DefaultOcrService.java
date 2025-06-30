package com.github.johypark97.varchivemacro.macro.core.scanner.ocr.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.infra.exception.OcrInitializationException;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.infra.model.DefaultPixImage;
import java.nio.file.Path;

public class DefaultOcrService implements OcrService {
    private final OcrWrapper ocrWrapper;

    public DefaultOcrService(Path traineddataDirectoryPath, String language)
            throws OcrInitializationException {
        try {
            ocrWrapper = new DefaultOcrWrapper(traineddataDirectoryPath, language);
        } catch (OcrInitializationError e) {
            throw new OcrInitializationException(e);
        }
    }

    @Override
    public String run(PixImage pixImage) {
        if (pixImage instanceof DefaultPixImage image) {
            return ocrWrapper.run(image.pixWrapper.pixInstance);
        }

        throw new UnsupportedOperationException(pixImage.getClass().getName());
    }

    @Override
    public void close() {
        ocrWrapper.close();
    }
}
