package com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import java.util.Map;

public interface SongCaptureLinkRepository {
    boolean isEmpty();

    void deleteAll();

    void save(SongCaptureLink link);

    SongCaptureLink remove(Song song, CaptureEntry captureEntry);

    Map<Song, Map<CaptureEntry, SongCaptureLink>> groupBySong();

    Map<CaptureEntry, Map<Song, SongCaptureLink>> groupByCaptureEntry();
}
