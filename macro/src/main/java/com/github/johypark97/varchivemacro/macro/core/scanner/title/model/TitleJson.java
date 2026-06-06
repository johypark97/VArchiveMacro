package com.github.johypark97.varchivemacro.macro.core.scanner.title.model;

import com.google.gson.annotations.Expose;
import java.util.List;

public record TitleJson(
        @Expose List<ClippedTitleJson> clipped,
        @Expose List<RemappedTitleJson> remap,
        @Expose List<RemoteTitleJson> remoteTitle
) {
}
