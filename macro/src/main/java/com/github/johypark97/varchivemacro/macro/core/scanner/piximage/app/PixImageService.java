package com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app;

import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.service.PixImagePreprocessService;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.infra.model.DefaultPixImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.infra.service.DefaultPixImagePreprocessService;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PixImageService implements PixImagePreprocessService {
    private final PixImagePreprocessService pixImagePreprocessService =
            new DefaultPixImagePreprocessService();

    public PixImage createPixImage(BufferedImage bufferedImage)
            throws IOException, PixImageException {
        return new DefaultPixImage(ImageConverter.imageToPngBytes(bufferedImage));
    }

    public PixImage createPixImage(byte[] pngByteArray) throws PixImageException {
        return new DefaultPixImage(pngByteArray);
    }

    @Override
    public void preprocessCell(PixImage pixImage) throws PixImageException {
        pixImagePreprocessService.preprocessCell(pixImage);
    }

    @Override
    public void preprocessTitle(PixImage pixImage) throws PixImageException {
        pixImagePreprocessService.preprocessTitle(pixImage);
    }
}
