package com.github.johypark97.varchivemacro.macro.core.scanner.api.record.fetch;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.Button;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public record FetchSuccessJson(@Expose boolean success,
                               @Expose @SerializedName("nickname") String djName,
                               @Expose Button button,
                               @Expose int count,
                               @Expose List<FetchRecordJson> records) {
}
