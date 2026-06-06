package com.github.johypark97.varchivemacro.macro.core.scanner.song.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Category;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Pack;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import java.util.List;
import java.util.Map;

public class SongService {
    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public boolean isEmpty() {
        return songRepository.isEmpty();
    }

    public Song findSongById(int songId) {
        return songRepository.findSongById(songId);
    }

    public List<Song> findAllSong() {
        return songRepository.findAllSong();
    }

    public List<Pack> findAllPack() {
        return songRepository.findAllPack();
    }

    public List<Category> findAllCategory() {
        return songRepository.findAllCategory();
    }

    public Map<Pack, List<Song>> groupSongByPack() {
        return songRepository.groupSongByPack();
    }

    public Map<Category, List<Song>> groupSongByCategory() {
        return songRepository.groupSongByCategory();
    }

    public List<Song> filterSongByDuplicateTitle() {
        return songRepository.filterSongByDuplicateTitle();
    }
}
