package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

class ImageWritingService {
    private final ExecutorService executor =
            new ThreadPoolExecutor(1, 2, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(2));

    public void shutdown() {
        executor.shutdown();
    }

    public void shutdownNow() {
        executor.shutdownNow();
    }

    public void await() throws InterruptedException {
        if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
            throw new RuntimeException("unexpected timeout");
        }
    }

    public void execute(ScanData data, BufferedImage image) {
        executor.execute(() -> {
            try {
                Path path = data.getImagePath();
                Files.deleteIfExists(path);
                Files.createDirectories(path);
                ImageIO.write(image, ScanData.FORMAT, path.toFile());

                data.setStatus("image saved");
            } catch (Exception e) {
                data.setException(e);
            }
        });
    }
}
