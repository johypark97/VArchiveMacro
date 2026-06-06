package com.github.johypark97.varchivemacro.macro.core.scanner.title.utility;

import com.google.common.base.CharMatcher;

public class TitleNormalizer {
    private static final char RIGHT_SINGLE_QUOTATION_MARK = '\u2019';

    public static String normalizeTitle_training(String value) {
        return value.replace('l', 'I');
    }

    public static String normalizeTitle_recognition(String value) {
        String s = normalizeTitle_training(value);
        s = CharMatcher.whitespace().removeFrom(s);
        s = s.replace(RIGHT_SINGLE_QUOTATION_MARK, '\'');

        return s;
    }
}
