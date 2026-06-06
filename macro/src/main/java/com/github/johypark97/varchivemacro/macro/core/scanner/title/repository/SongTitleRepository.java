package com.github.johypark97.varchivemacro.macro.core.scanner.title.repository;

import java.util.Optional;

public interface SongTitleRepository {
    Optional<String> findClippedTitle(int songId);

    Optional<String> findRemoteTitle(int songId);

    Optional<String> findRemappedTitle(String scannedTitle);
}
