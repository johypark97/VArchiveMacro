package com.github.johypark97.varchivemacro.macro.application.utility;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Function;

public class UnicodeFilter implements Function<String, Boolean> {
    private static final Function<String, String> NORMALIZER =
            x -> Normalizer.normalize(x.toLowerCase(Locale.ENGLISH), Normalizer.Form.NFKD);

    private final String filter;

    public UnicodeFilter(String filter) {
        this.filter = NORMALIZER.apply(filter);
    }

    @Override
    public Boolean apply(String s) {
        return NORMALIZER.apply(s).contains(filter);
    }
}
