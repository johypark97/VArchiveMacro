package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class DefaultImageCacheManager implements ImageCacheManager {
    private static final String FORMAT = "png";

    private final Path cacheDir;

    public DefaultImageCacheManager(Path cacheDir) {
        this.cacheDir = cacheDir;
    }

    @Override
    public String getFormat() {
        return FORMAT;
    }

    @Override
    public Path createPath(int number) {
        return cacheDir.resolve(String.format("%04d.%s", number, FORMAT));
    }

    @Override
    public void saveImage(int number, BufferedImage image) throws IOException {
        Path filePath = createPath(number);

        Path dirPath = filePath.getParent();
        if (dirPath != null) {
            Files.createDirectories(dirPath);
        }

        Files.deleteIfExists(filePath);
        ImageIO.write(image, FORMAT, filePath.toFile());
    }

    @Override
    public BufferedImage loadImage(int number) throws IOException {
        return ImageIO.read(createPath(number).toFile());
    }
}
