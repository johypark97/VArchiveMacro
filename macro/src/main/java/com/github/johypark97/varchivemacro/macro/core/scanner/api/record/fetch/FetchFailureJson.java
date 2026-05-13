package com.github.johypark97.varchivemacro.macro.core.scanner.api.record.fetch;

import com.google.gson.annotations.Expose;

/**
 * @param success   nullable
 * @param errorCode nullable
 */
public record FetchFailureJson(@Expose Boolean success,
                               @Expose Integer errorCode,
                               @Expose String message) {
}
