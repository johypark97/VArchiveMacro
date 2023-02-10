package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScanData.Status;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class ImageWritingService {
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int WORK_QUEUE_CAPACITY = 4;

    private final ExecutorService executor =
            new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(WORK_QUEUE_CAPACITY));

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
                data.saveImage(image);
                data.setStatus(Status.CACHED);
            } catch (Exception e) {
                data.setException(e);
            }
        });
    }
}
