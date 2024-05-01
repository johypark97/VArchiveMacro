package com.github.johypark97.varchivemacro.macro.fxgui.model.manager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

public class CacheManager {
    private static final String FORMAT = "png";
    private static final String MARKER_FILE_NAME = ".vamacro";

    private final Path cacheDirectoryPath;

    public CacheManager(Path cacheDirectoryPath) {
        this.cacheDirectoryPath = cacheDirectoryPath;
    }

    private static void createMarkerFile(Path path) throws IOException {
        // write the present time to prevent being deleted by the empty file automatic deletion
        Files.writeString(path, ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
    }

    public void validate() throws IOException {
        if (!Files.exists(cacheDirectoryPath)) {
            return;
        }

        if (!Files.isDirectory(cacheDirectoryPath)) {
            throw new NotDirectoryException(cacheDirectoryPath.toString());
        }

        if (Files.exists(cacheDirectoryPath.resolve(MARKER_FILE_NAME))) {
            return;
        }

        boolean isEmpty;
        try (Stream<Path> stream = Files.list(cacheDirectoryPath)) {
            isEmpty = stream.findAny().isEmpty();
        }

        if (!isEmpty) {
            throw new DirectoryNotEmptyException(cacheDirectoryPath.toString());
        }
    }

    public void prepare() throws IOException {
        validate();

        Path markerPath = cacheDirectoryPath.resolve(MARKER_FILE_NAME);

        if (!Files.exists(cacheDirectoryPath)) {
            Files.createDirectories(cacheDirectoryPath);
            createMarkerFile(markerPath);
            return;
        }

        boolean isEmpty;
        try (Stream<Path> stream = Files.list(cacheDirectoryPath)) {
            isEmpty = stream.findAny().isEmpty();
        }

        if (isEmpty) {
            createMarkerFile(markerPath);
            return;
        }

        List<Path> list;
        Set<Path> excludeSet = Set.of(cacheDirectoryPath, markerPath);
        try (Stream<Path> stream = Files.walk(cacheDirectoryPath)) {
            list = stream.filter(x -> !excludeSet.contains(x)).sorted(Comparator.reverseOrder())
                    .toList();
        }

        for (Path path : list) {
            Files.delete(path);
        }
    }

    public void write(int id, BufferedImage image) throws IOException {
        Path path = createPath(id);

        Files.deleteIfExists(path);
        ImageIO.write(image, FORMAT, path.toFile());
    }

    public BufferedImage read(int id) throws IOException {
        return ImageIO.read(createPath(id).toFile());
    }

    public Path createPath(int id) {
        return cacheDirectoryPath.resolve(String.format("%04d.%s", id, FORMAT));
    }
}
