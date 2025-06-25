package com.github.johypark97.varchivemacro.macro.common.license.infra.model;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

public record LicenseJson(@Expose String path, @Expose String url) {
    public static class GsonTypeToken extends TypeToken<Map<String, LicenseJson>> {
    }
}
