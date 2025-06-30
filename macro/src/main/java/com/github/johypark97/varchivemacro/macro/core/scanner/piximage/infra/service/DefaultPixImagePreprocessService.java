package com.github.johypark97.varchivemacro.macro.core.scanner.piximage.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.service.PixImagePreprocessService;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.infra.model.DefaultPixImage;

public class DefaultPixImagePreprocessService implements PixImagePreprocessService {
    @Override
    public void preprocessCell(PixImage pixImage) throws PixImageException {
        try {
            if (pixImage instanceof DefaultPixImage image) { // NOPMD
                PixPreprocessor.preprocessCell(image.pixWrapper);
            } else {
                throw new UnsupportedOperationException(pixImage.getClass().getName());
            }
        } catch (PixError e) {
            throw new PixImageException(e);
        }
    }

    @Override
    public void preprocessTitle(PixImage pixImage) throws PixImageException {
        try {
            if (pixImage instanceof DefaultPixImage image) { // NOPMD
                PixPreprocessor.preprocessTitle(image.pixWrapper);
            } else {
                throw new UnsupportedOperationException(pixImage.getClass().getName());
            }
        } catch (PixError e) {
            throw new PixImageException(e);
        }
    }
}
