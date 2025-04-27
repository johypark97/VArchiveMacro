package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Song;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongCaptureLinkRepository;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Map;

public class DefaultSongCaptureLinkRepository implements SongCaptureLinkRepository {
    private final Table<Song, CaptureEntry, SongCaptureLink> linkTable = HashBasedTable.create();

    @Override
    public boolean isEmpty() {
        return linkTable.isEmpty();
    }

    @Override
    public void deleteAll() {
        linkTable.clear();
    }

    @Override
    public void save(SongCaptureLink link) {
        linkTable.put(link.song(), link.captureEntry(), link);
    }

    @Override
    public SongCaptureLink remove(Song song, CaptureEntry captureEntry) {
        return linkTable.remove(song, captureEntry);
    }

    @Override
    public Map<Song, Map<CaptureEntry, SongCaptureLink>> groupBySong() {
        return linkTable.rowMap();
    }

    @Override
    public Map<CaptureEntry, Map<Song, SongCaptureLink>> groupByCaptureEntry() {
        return linkTable.columnMap();
    }
}
