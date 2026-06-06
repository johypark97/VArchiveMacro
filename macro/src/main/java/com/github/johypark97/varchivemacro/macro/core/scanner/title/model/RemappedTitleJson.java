package com.github.johypark97.varchivemacro.macro.core.scanner.title.model;

import com.google.gson.annotations.Expose;
import java.util.List;

public record RemappedTitleJson(@Expose String to, @Expose List<String> from) {
}
