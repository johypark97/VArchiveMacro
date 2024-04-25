package com.github.johypark97.varchivemacro.macro.fxgui.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

public record License(@Expose String path, @Expose String url) {
    public static class GsonTypeToken extends TypeToken<Map<String, License>> {
    }
}
