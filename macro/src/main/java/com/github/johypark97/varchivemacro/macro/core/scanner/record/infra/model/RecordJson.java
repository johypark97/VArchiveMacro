package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.model;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public record RecordJson(
        @Expose int id,
        @Expose ButtonJson button,
        @Expose PatternJson pattern,
        @Expose float rate,
        @Expose boolean maxCombo
) {
    public static class GsonListTypeToken extends TypeToken<List<RecordJson>> {
    }
}
