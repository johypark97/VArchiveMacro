package com.github.johypark97.varchivemacro.macro.infrastructure.songtitle;

import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.util.function.Function;

public class SongTitleNormalizer implements Function<String, String> {
    @Override
    public String apply(String s) {
        return TitleTool.normalizeTitle_recognition(s);
    }
}
