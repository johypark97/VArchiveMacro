package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.nio.file.Path;

public class CacheHelper {
    public static final String FORMAT = "png";

    private static String makeFilename(int id) {
        return String.format("%05d.%s", id, FORMAT);
    }

    public static Path createPath(Path baseDir, LocalSong song) {
        return baseDir.resolve(makeFilename(song.id()));
    }
}
