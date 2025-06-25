package com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory;

import com.github.johypark97.varchivemacro.macro.core.scanner.cache.infra.CaptureImageCache;
import java.io.IOException;

public interface CaptureImageCacheFactory {
    CaptureImageCache create(String cacheDirectory) throws IOException;
}
