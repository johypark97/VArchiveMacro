package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class CacheHelper {
    public static final String BOX_FORMAT = "box";
    public static final String GT_FORMAT = "gt.txt";
    public static final String IMAGE_FORMAT = "png";

    public static Path createBoxPath(Path baseDir, LocalSong song, int number) {
        return baseDir.resolve(makeFilename(song.id(), number, BOX_FORMAT));
    }

    public static Path createGtPath(Path baseDir, LocalSong song, int number) {
        return baseDir.resolve(makeFilename(song.id(), number, GT_FORMAT));
    }

    public static Path createImagePath(Path baseDir, LocalSong song, int number) {
        return baseDir.resolve(makeFilename(song.id(), number, IMAGE_FORMAT));
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

    private static String makeFilename(int id, int number, String format) {
        return String.format("%05d_%d.%s", id, number, format);
    }
}
