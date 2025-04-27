package com.github.johypark97.varchivemacro.macro.domain.scanner.repository;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Song;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongCaptureLink;
import java.util.Map;

public interface SongCaptureLinkRepository {
    boolean isEmpty();

    void deleteAll();

    void save(SongCaptureLink link);

    SongCaptureLink remove(Song song, CaptureEntry captureEntry);

    Map<Song, Map<CaptureEntry, SongCaptureLink>> groupBySong();

    Map<CaptureEntry, Map<Song, SongCaptureLink>> groupByCaptureEntry();
}
