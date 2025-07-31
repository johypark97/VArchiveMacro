package com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.infra.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository.CaptureImageRepository;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import javax.imageio.ImageIO;

public class DiskCaptureImageRepository implements CaptureImageRepository {
    private static final String IMAGE_FORMAT = "png";
    private static final String MARKER_FILENAME = ".vamacro";

    private final Path cacheDirectoryPath;

    public DiskCaptureImageRepository(Path cacheDirectoryPath) throws IOException {
        validate(cacheDirectoryPath);
        prepare(cacheDirectoryPath);

        this.cacheDirectoryPath = cacheDirectoryPath;
    }

    public static void validate(Path cacheDirectoryPath) throws IOException {
        if (!Files.exists(cacheDirectoryPath)) {
            return;
        }

        if (!Files.isDirectory(cacheDirectoryPath)) {
            throw new NotDirectoryException(cacheDirectoryPath.toString());
        }

        if (Files.exists(createMarkerFilePath(cacheDirectoryPath))) {
            return;
        }

        if (!isDirectoryEmpty(cacheDirectoryPath)) {
            throw new DirectoryNotEmptyException(cacheDirectoryPath.toString());
        }
    }

    private static void prepare(Path cacheDirectoryPath) throws IOException {
        Path markerFilePath = createMarkerFilePath(cacheDirectoryPath);

        if (!Files.exists(cacheDirectoryPath)) {
            Files.createDirectories(cacheDirectoryPath);
            createMarkerFile(markerFilePath);
            return;
        }

        if (isDirectoryEmpty(cacheDirectoryPath)) {
            createMarkerFile(markerFilePath);
        }
    }

    private static boolean isDirectoryEmpty(Path path) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            return !stream.iterator().hasNext();
        }
    }

    private static Path createMarkerFilePath(Path cacheDirectoryPath) {
        return cacheDirectoryPath.resolve(MARKER_FILENAME);
    }

    private static void createMarkerFile(Path markerFilePath) throws IOException {
        // write the present time to prevent being deleted by the empty file automatic deletion
        Files.writeString(markerFilePath,
                ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
    }

    protected Path createImageFilePath(int id) {
        return cacheDirectoryPath.resolve(String.format("%04d.%s", id, IMAGE_FORMAT));
    }

    @Override
    public boolean isEmpty() throws IOException {
        Path markerFilepath = createMarkerFilePath(cacheDirectoryPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(cacheDirectoryPath,
                entry -> !entry.equals(markerFilepath))) {
            return !stream.iterator().hasNext();
        }
    }

    @Override
    public void deleteAll() throws IOException {
        Path markerFilePath = createMarkerFilePath(cacheDirectoryPath);

        Files.walkFileTree(cacheDirectoryPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (!file.equals(markerFilePath)) {
                    Files.delete(file);
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                if (!dir.equals(cacheDirectoryPath)) {
                    Files.delete(dir);
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void save(int id, BufferedImage image) throws IOException {
        Path path = createImageFilePath(id);

        Files.deleteIfExists(path);
        ImageIO.write(image, IMAGE_FORMAT, path.toFile());
    }

    @Override
    public BufferedImage findById(int id) throws IOException {
        return ImageIO.read(createImageFilePath(id).toFile());
    }
}
