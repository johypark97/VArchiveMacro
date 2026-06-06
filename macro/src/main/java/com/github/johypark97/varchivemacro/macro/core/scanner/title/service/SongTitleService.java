package com.github.johypark97.varchivemacro.macro.core.scanner.title.service;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.repository.SongTitleRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.utility.TitleNormalizer;

public class SongTitleService {
    private final SongTitleRepository songTitleRepository;

    public SongTitleService(SongTitleRepository songTitleRepository) {
        this.songTitleRepository = songTitleRepository;
    }

    public String getClippedTitleOrDefault(Song song) {
        return songTitleRepository.findClippedTitle(song.songId()).orElse(song.title());
    }

    public String getRemoteTitleOrDefault(Song song) {
        return songTitleRepository.findRemoteTitle(song.songId()).orElse(song.title());
    }

    public String remapScannedTitle(String value) {
        return songTitleRepository.findRemappedTitle(value).orElse(value);
    }

    public String normalizeTitle(String value) {
        return TitleNormalizer.normalizeTitle_recognition(value);
    }
}
