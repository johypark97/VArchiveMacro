package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.ScanDataRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.service.CaptureImageCacheService;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultScanDataRepository implements ScanDataRepository {
    private final List<CaptureData> captureDataList = new ArrayList<>();
    private final List<SongData> songDataList = new ArrayList<>();

    @Override
    public List<CaptureData> copyCaptureDataList() {
        return List.copyOf(captureDataList);
    }

    @Override
    public List<SongData> copySongDataList() {
        return List.copyOf(songDataList);
    }

    @Override
    public boolean isEmpty() {
        return captureDataList.isEmpty() && songDataList.isEmpty();
    }

    @Override
    public void clear() {
        captureDataList.clear();
        songDataList.clear();
    }

    @Override
    public SongData getSongData(int index) {
        return songDataList.get(index);
    }

    @Override
    public SongData createSongData(Song song, String normalizedTitle) {
        int id = songDataList.size();

        SongData data = new SongData(id, song, normalizedTitle);
        songDataList.add(data);

        return data;
    }

    @Override
    public CaptureData getCaptureData(int index) {
        return captureDataList.get(index);
    }

    @Override
    public CaptureData createCaptureData() {
        int id = captureDataList.size();

        CaptureData data = new CaptureData(id);
        captureDataList.add(data);

        return data;
    }

    @Override
    public BufferedImage getCaptureImage(int index, String cacheDirectory) throws IOException {
        CaptureImageCacheService captureImageCacheService =
                new CaptureImageCacheService(cacheDirectory);

        try {
            return captureImageCacheService.read(index);
        } catch (IOException e) {
            captureDataList.get(index).exception.set(e);
            throw e;
        }
    }
}
