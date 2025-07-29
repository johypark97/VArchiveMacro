package com.github.johypark97.varchivemacro.macro.common.programdata.domain;

import java.time.ZonedDateTime;
import java.util.List;

public record DataVersion(ZonedDateTime version, List<DataFile> fileList) {
    public record DataFile(String path, String requestPath, String hash) {
    }
}
