package com.github.johypark97.varchivemacro.macro.core.scanner.link.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import java.util.Map;

public class SongCaptureLinkService implements SongCaptureLinkRepository {
    private final SongCaptureLinkRepository songCaptureLinkRepository;

    public SongCaptureLinkService(SongCaptureLinkRepository songCaptureLinkRepository) {
        this.songCaptureLinkRepository = songCaptureLinkRepository;
    }

    @Override
    public boolean isEmpty() {
        return songCaptureLinkRepository.isEmpty();
    }

    @Override
    public void deleteAll() {
        songCaptureLinkRepository.deleteAll();
    }

    @Override
    public void save(SongCaptureLink link) {
        songCaptureLinkRepository.save(link);
    }

    @Override
    public SongCaptureLink remove(Song song, CaptureEntry captureEntry) {
        return songCaptureLinkRepository.remove(song, captureEntry);
    }

    @Override
    public Map<Song, Map<CaptureEntry, SongCaptureLink>> groupBySong() {
        return songCaptureLinkRepository.groupBySong();
    }

    @Override
    public Map<CaptureEntry, Map<Song, SongCaptureLink>> groupByCaptureEntry() {
        return songCaptureLinkRepository.groupByCaptureEntry();
    }
}
