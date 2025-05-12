package com.github.johypark97.varchivemacro.macro.application.scanner.factory;

import com.github.johypark97.varchivemacro.macro.infrastructure.cache.CaptureImageCache;
import java.io.IOException;

public interface CaptureImageCacheFactory {
    CaptureImageCache create(String cacheDirectory) throws IOException;
}
