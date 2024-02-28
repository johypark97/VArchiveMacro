package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.common.Enums;
import com.google.common.base.Converter;

public enum Pattern {
    NM(0, "NORMAL", "NM"), HD(1, "HARD", "HD"), MX(2, "MAXIMUM", "MX"), SC(3, "SC", "SC");

    private static final PatternConverter converter = new PatternConverter();

    private final String fullName;
    private final String shortName;
    private final int weight;

    Pattern(int w, String f, String s) {
        fullName = f;
        shortName = s;
        weight = w;
    }

    public static Pattern valueOf(Enums.Pattern pattern) {
        return converter.reverse().convert(pattern);
    }

    public String getShortName() {
        return shortName;
    }

    public int getWeight() {
        return weight;
    }

    public Enums.Pattern toLib() {
        return converter.convert(this);
    }

    @Override
    public String toString() {
        return fullName;
    }

    public static class PatternConverter extends Converter<Pattern, Enums.Pattern> {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        protected Enums.Pattern doForward(Pattern pattern) {
            return switch (pattern) {
                case NM -> Enums.Pattern.NM;
                case HD -> Enums.Pattern.HD;
                case MX -> Enums.Pattern.MX;
                case SC -> Enums.Pattern.SC;
            };
        }

        @Override
        protected Pattern doBackward(Enums.Pattern pattern) {
            return switch (pattern) {
                case NM -> NM;
                case HD -> HD;
                case MX -> MX;
                case SC -> SC;
            };
        }

        @Override
        public boolean equals(Object object) {
            return super.equals(object);
        }
    }
}
