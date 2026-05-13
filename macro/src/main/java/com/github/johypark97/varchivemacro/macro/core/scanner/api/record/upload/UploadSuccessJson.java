package com.github.johypark97.varchivemacro.macro.core.scanner.api.record.upload;

import com.google.gson.annotations.Expose;

public record UploadSuccessJson(@Expose boolean success, @Expose boolean update) {
}
