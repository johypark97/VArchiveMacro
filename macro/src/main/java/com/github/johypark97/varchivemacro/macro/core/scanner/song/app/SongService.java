package com.github.johypark97.varchivemacro.macro.core.scanner.song.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import java.util.List;
import java.util.Map;

public class SongService implements SongRepository {
    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public boolean isEmpty() {
        return songRepository.isEmpty();
    }

    @Override
    public Song findSongById(int songId) {
        return songRepository.findSongById(songId);
    }

    @Override
    public List<Song> findAllSong() {
        return songRepository.findAllSong();
    }

    @Override
    public List<Song.Pack> findAllPack() {
        return songRepository.findAllPack();
    }

    @Override
    public List<Song.Pack.Category> findAllCategory() {
        return songRepository.findAllCategory();
    }

    @Override
    public Map<Song.Pack, List<Song>> groupSongByPack() {
        return songRepository.groupSongByPack();
    }

    @Override
    public Map<Song.Pack.Category, List<Song>> groupSongByCategory() {
        return songRepository.groupSongByCategory();
    }

    @Override
    public List<Song> filterSongByDuplicateTitle() {
        return songRepository.filterSongByDuplicateTitle();
    }
}
