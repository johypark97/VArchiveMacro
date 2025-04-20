package com.github.johypark97.varchivemacro.macro.infrastructure.github.data;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public record GitHubContent(
        // @formatter:off
        @Expose @SerializedName("download_url") String downloadUrl,
        @Expose String content,
        @Expose String name,
        @Expose String type
        // @formatter:on
) {
    public static GitHubContent from(String data) {
        Gson gson = GsonWrapper.newGsonBuilder_general().create();
        return gson.fromJson(data, GitHubContent.class);
    }
}
