package com.github.johypark97.varchivemacro.macro.integration.provider;

import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.DefaultOcrFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.OcrFactory;
import java.nio.file.Path;

public class FactoryProvider {
    public static OcrFactory createSongTitleOcrFactory() {
        return new DefaultOcrFactory(Path.of("data"), "djmax");
    }
}
