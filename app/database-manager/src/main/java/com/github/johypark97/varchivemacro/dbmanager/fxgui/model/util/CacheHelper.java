package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.util;

import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class CacheHelper {
    public static final String GT_FORMAT = "gt.txt";
    public static final String IMAGE_FORMAT = "png";

    public static Path createGtPath(Path baseDir, LocalDlcSong song) {
        return baseDir.resolve(makeFilename(song.id, GT_FORMAT));
    }

    public static Path createImagePath(Path baseDir, LocalDlcSong song) {
        return baseDir.resolve(makeFilename(song.id, IMAGE_FORMAT));
    }

    public static void clearAndReadyDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        } else if (!Files.isDirectory(path)) {
            throw new RuntimeException("path is not a directory");
        } else {
            List<Path> list;
            try (Stream<Path> stream = Files.walk(path)) {
                list = stream.sorted(Comparator.reverseOrder()).toList();
            }

            Path absDir = path.toAbsolutePath();
            for (Path p : list) {
                if (!absDir.equals(p.toAbsolutePath())) {
                    Files.delete(p);
                }
            }
        }
    }

    private static String makeFilename(int id, String format) {
        return String.format("%04d.%s", id, format);
    }
}