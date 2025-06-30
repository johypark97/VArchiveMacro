package com.github.johypark97.varchivemacro.macro.integration.app.scanner.task;

import com.github.johypark97.varchivemacro.lib.desktop.AwtRobotHelper;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureBound;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.app.SongCaptureLinkService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCollectionScanTask extends CollectionScanTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCollectionScanTask.class);

    private static final int REJECTED_SLEEP_TIME = 1000;

    private final CaptureImageService captureImageService;

    private final ScannerConfig config;

    private final AtomicReference<Exception> imageCachingTaskException = new AtomicReference<>();
    private final Robot robot;

    private CollectionArea collectionArea;
    private ImageCachingService imageCachingService;

    public DefaultCollectionScanTask(CaptureImageService captureImageService,
            CaptureService captureService, PixImageService pixImageService,
            SongCaptureLinkService songCaptureLinkService, SongService songService,
            SongTitleService songTitleService, OcrServiceFactory songTitleOcrServiceFactory,
            ScannerConfig config, Set<String> selectedCategorySet) {
        super(captureService, pixImageService, songCaptureLinkService, songService,
                songTitleService, songTitleOcrServiceFactory, selectedCategorySet);

        this.captureImageService = captureImageService;

        this.config = config;

        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    private CollectionArea createCollectionArea() throws NotSupportedResolutionException {
        BufferedImage image = AwtRobotHelper.captureScreenshot(robot);
        Dimension resolution = new Dimension(image.getWidth(), image.getHeight());

        return CollectionAreaFactory.create(resolution);
    }

    @Override
    protected BufferedImage captureScreen() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(config.captureDelay());
        return AwtRobotHelper.captureScreenshot(robot);
    }

    @Override
    protected CaptureBound getTitleBound() {
        Rectangle r = collectionArea.getTitle();
        return new CaptureBound(r.x, r.y, r.width, r.height);
    }

    @Override
    protected void moveToNextCategory() throws InterruptedException {
        AwtRobotHelper.tabKey(robot, config.keyHoldTime(), KeyEvent.VK_SPACE);
    }

    @Override
    protected void moveToNextSong() throws InterruptedException {
        AwtRobotHelper.tabKey(robot, config.keyHoldTime(), KeyEvent.VK_DOWN);
    }

    @Override
    protected void writeImage(int captureId, BufferedImage captureImage)
            throws InterruptedException {
        while (true) {
            // interrupt the main task when an exception has occurred while caching images
            if (imageCachingTaskException.get() != null) {
                throw new InterruptedException();
            }

            try {
                imageCachingService.execute(() -> {
                    try {
                        captureImageService.save(captureId, captureImage);
                    } catch (IOException e) {
                        LOGGER.atError().setCause(e).log("writeImage() Exception");
                        imageCachingTaskException.compareAndSet(null, e);
                    }
                });

                break;
            } catch (RejectedExecutionException ignored) {
            }

            TimeUnit.MILLISECONDS.sleep(REJECTED_SLEEP_TIME);
        }
    }

    @Override
    protected Void callTask() throws Exception {
        // check if the screen resolution is supported using whether an exception occurs
        collectionArea = createCollectionArea();

        // delete all previous images
        captureImageService.deleteAll();

        try {
            imageCachingService = new ImageCachingService();

            // run super task
            super.callTask();
        } finally {
            imageCachingService.shutdown();
        }

        // wait for imageCachingService to terminate
        try {
            imageCachingService.awaitTermination();
        } catch (InterruptedException e) {
            imageCachingService.shutdownNow();
        }

        // throws an exception that occurred while caching images
        Exception e = imageCachingTaskException.get();
        if (e != null) {
            throw e;
        }

        return null;
    }

    public static class ImageCachingService {
        private static final int CORE_POOL_SIZE = 1;
        private static final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors();
        private static final int WORK_QUEUE_CAPACITY = 4;

        private final ExecutorService executorService =
                new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1, TimeUnit.MINUTES,
                        new ArrayBlockingQueue<>(WORK_QUEUE_CAPACITY));

        public void shutdown() {
            executorService.shutdown();
        }

        public void shutdownNow() {
            executorService.shutdownNow();
        }

        public void awaitTermination() throws InterruptedException {
            if (!executorService.awaitTermination(1, TimeUnit.DAYS)) {
                throw new AssertionError("Unexpected timeout");
            }
        }

        public void execute(Runnable command) {
            executorService.execute(command);
        }
    }
}
