package com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.service;

import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;

public interface PixImagePreprocessService {
    void preprocessCell(PixImage pixImage) throws PixImageException;

    void preprocessTitle(PixImage pixImage) throws PixImageException;
}
