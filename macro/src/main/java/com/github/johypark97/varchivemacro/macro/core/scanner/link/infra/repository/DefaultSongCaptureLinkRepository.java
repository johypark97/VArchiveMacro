package com.github.johypark97.varchivemacro.macro.core.scanner.link.infra.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
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
