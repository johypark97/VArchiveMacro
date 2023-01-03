package com.github.johypark97.varchivemacro.lib.common.json;

import com.google.gson.GsonBuilder;

public class GsonWrapper {
    public static GsonBuilder newGsonBuilder_base() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
    }

    public static GsonBuilder newGsonBuilder_dump() {
        return newGsonBuilder_base().disableHtmlEscaping().serializeNulls().setPrettyPrinting();
    }
}
