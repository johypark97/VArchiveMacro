package com.github.johypark97.varchivemacro.macro.domain;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public interface ScanDataDomain {
    List<CaptureData> copyCaptureDataList();

    List<SongData> copySongDataList();

    boolean isEmpty();

    void clear();

    SongData getSongData(int index);

    SongData createSongData(SongDatabase.Song song, String normalizedTitle);

    CaptureData getCaptureData(int index);

    CaptureData createCaptureData();

    BufferedImage getCaptureImage(int index, String cacheDirectory) throws IOException;
}
