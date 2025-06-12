package com.github.johypark97.varchivemacro.macro.common.converter;

import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.RecordPattern;

public class RecordPatternConverter {
    public static RecordPattern toDomain(Enums.Pattern pattern) {
        return switch (pattern) {
            case NM -> RecordPattern.NORMAL;
            case HD -> RecordPattern.HARD;
            case MX -> RecordPattern.MAXIMUM;
            case SC -> RecordPattern.SC;
        };
    }

    public static Enums.Pattern toLib(RecordPattern pattern) {
        return switch (pattern) {
            case NORMAL -> Enums.Pattern.NM;
            case HARD -> Enums.Pattern.HD;
            case MAXIMUM -> Enums.Pattern.MX;
            case SC -> Enums.Pattern.SC;
        };
    }
}
