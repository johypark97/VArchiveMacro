package com.github.johypark97.varchivemacro.macro.application.scanner.factory;

import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.infrastructure.cache.CaptureImageCache;
import com.github.johypark97.varchivemacro.macro.infrastructure.cache.PngCaptureImageCache;
import java.io.IOException;
import java.nio.file.Path;

public class DefaultCaptureImageCacheFactory implements CaptureImageCacheFactory {
    @Override
    public CaptureImageCache create(String cacheDirectory) throws IOException {
        Path path = PathValidator.validateAndConvert(cacheDirectory);
        return new PngCaptureImageCache(path);
    }
}
