package com.github.johypark97.varchivemacro.macro.application.provider;

import com.github.johypark97.varchivemacro.macro.application.scanner.factory.CaptureImageCacheFactory;
import com.github.johypark97.varchivemacro.macro.application.scanner.factory.DefaultCaptureImageCacheFactory;

public class FactoryProvider {
    public static CaptureImageCacheFactory createCaptureImageCacheFactory() {
        return new DefaultCaptureImageCacheFactory();
    }
}
