package com.github.johypark97.varchivemacro.macro.core.scanner.api.record.upload;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.Button;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.Pattern;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public record UploadRequestJson(@Expose @SerializedName("name") String title,
                                @Expose String dlc,
                                @Expose String composer,
                                @Expose int button,
                                @Expose String pattern,
                                @Expose @SerializedName("score") float rate,
                                @Expose int maxCombo) {
    public UploadRequestJson(String title,
                             Button button,
                             Pattern pattern,
                             float rate,
                             boolean isMaxCombo) {
        Objects.requireNonNull(title);

        if (rate < 0.0f || rate > 100.0f) {
            throw new IllegalArgumentException("Invalid rate: " + rate);
        }

        int maxCombo = (isMaxCombo || rate == 100.0f) ? 1 : 0;

        this(title, null, null, button.toInt(), pattern.getFullName(), rate, maxCombo);
    }

    public UploadRequestJson withDlc(String dlc) {
        return new UploadRequestJson(title, dlc, composer, button, pattern, rate, maxCombo);
    }

    public UploadRequestJson withComposer(String composer) {
        return new UploadRequestJson(title, dlc, composer, button, pattern, rate, maxCombo);
    }
}
