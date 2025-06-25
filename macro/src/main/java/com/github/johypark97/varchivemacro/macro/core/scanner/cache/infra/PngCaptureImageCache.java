package com.github.johypark97.varchivemacro.macro.core.scanner.cache.infra;

import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
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

public class PngCaptureImageCache implements CaptureImageCache {
    private static final String FORMAT = "png";
    private static final String MARKER_FILENAME = ".vamacro";

    private final Path cacheDirectoryPath;

    public PngCaptureImageCache(Path path) {
        cacheDirectoryPath = path;
    }

    public PngCaptureImageCache(String path) throws IOException {
        this(PathValidator.validateAndConvert(path));
    }

    protected void createMarkerFile(Path path) throws IOException {
        // write the present time to prevent being deleted by the empty file automatic deletion
        Files.writeString(path, ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
    }

    protected Path createFilePath(int id) {
        return cacheDirectoryPath.resolve(String.format("%04d.%s", id, FORMAT));
    }

    @Override
    public void validate() throws IOException {
        if (!Files.exists(cacheDirectoryPath)) {
            return;
        }

        if (!Files.isDirectory(cacheDirectoryPath)) {
            throw new NotDirectoryException(cacheDirectoryPath.toString());
        }

        if (Files.exists(cacheDirectoryPath.resolve(MARKER_FILENAME))) {
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

    @Override
    public void prepare() throws IOException {
        validate();

        Path markerPath = cacheDirectoryPath.resolve(MARKER_FILENAME);

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

    @Override
    public void write(int id, BufferedImage image) throws IOException {
        Path path = createFilePath(id);

        Files.deleteIfExists(path);
        ImageIO.write(image, FORMAT, path.toFile());
    }

    @Override
    public BufferedImage read(int id) throws IOException {
        return ImageIO.read(createFilePath(id).toFile());
    }
}
