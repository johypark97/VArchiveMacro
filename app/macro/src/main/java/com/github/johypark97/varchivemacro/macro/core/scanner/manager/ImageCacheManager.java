package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface ImageCacheManager {
    String getFormat();

    Path createPath(LocalSong song);

    void saveImage(LocalSong song, BufferedImage image) throws IOException;

    BufferedImage loadImage(LocalSong song) throws IOException;
}
