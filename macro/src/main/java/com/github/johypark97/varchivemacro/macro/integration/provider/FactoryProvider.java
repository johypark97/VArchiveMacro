package com.github.johypark97.varchivemacro.macro.integration.provider;

import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.DefaultOcrFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.DefaultSongTitleMapperFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.SongTitleMapperFactory;
import java.nio.file.Path;

public class FactoryProvider {
    public static OcrFactory createSongTitleOcrFactory() {
        return new DefaultOcrFactory(Path.of("data"), "djmax");
    }

    public static SongTitleMapperFactory createSongTitleMapperFactory() {
        return new DefaultSongTitleMapperFactory(Path.of("data/titles.json"));
    }
}
