package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;

public enum PatternJson {
    NM(RecordPattern.NORMAL),
    HD(RecordPattern.HARD),
    MX(RecordPattern.MAXIMUM),
    SC(RecordPattern.SC);

    private final RecordPattern domainPattern;

    PatternJson(RecordPattern domainPattern) {
        this.domainPattern = domainPattern;
    }

    public static PatternJson fromDomain(RecordPattern pattern) {
        return switch (pattern) {
            case NORMAL -> NM;
            case HARD -> HD;
            case MAXIMUM -> MX;
            case SC -> SC;
        };
    }

    public RecordPattern toDomain() {
        return domainPattern;
    }
}
