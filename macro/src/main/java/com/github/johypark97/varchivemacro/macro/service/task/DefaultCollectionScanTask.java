package com.github.johypark97.varchivemacro.macro.service.task;

import com.github.johypark97.varchivemacro.lib.desktop.AwtRobotHelper;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.repository.CacheRepository;
import com.github.johypark97.varchivemacro.macro.repository.ScanDataRepository;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultCollectionScanTask extends AbstractCollectionScanTask {
    private static final int REJECTED_SLEEP_TIME = 1000;

    private final Robot robot;
    private final String cacheDirectory;
    private final int captureDelay;
    private final int keyInputDuration;

    private CacheRepository cacheRepository;
    private CollectionArea collectionArea;
    private ImageCachingService imageCachingService;

    public DefaultCollectionScanTask(ScanDataRepository scanDataRepository,
            Map<String, List<Song>> categoryNameSongListMap, TitleTool titleTool,
            Set<String> selectedCategorySet, String cacheDirectory, int captureDelay,
            int keyInputDuration) {
        super(scanDataRepository, categoryNameSongListMap, titleTool, selectedCategorySet);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        this.cacheDirectory = cacheDirectory;
        this.captureDelay = captureDelay;
        this.keyInputDuration = keyInputDuration;
    }

    private CollectionArea createCollectionArea() throws NotSupportedResolutionException {
        BufferedImage image = AwtRobotHelper.captureScreenshot(robot);
        Dimension resolution = new Dimension(image.getWidth(), image.getHeight());

        return CollectionAreaFactory.create(resolution);
    }

    @Override
    protected void moveToNextCategory() throws InterruptedException {
        AwtRobotHelper.tabKey(robot, keyInputDuration, KeyEvent.VK_SPACE);
    }

    @Override
    protected void moveToNextSong() throws InterruptedException {
        AwtRobotHelper.tabKey(robot, keyInputDuration, KeyEvent.VK_DOWN);
    }

    @Override
    protected BufferedImage captureScreenshot(CaptureData data) throws Exception {
        TimeUnit.MILLISECONDS.sleep(captureDelay);

        BufferedImage image = AwtRobotHelper.captureScreenshot(robot);

        Runnable command = () -> {
            try {
                cacheRepository.write(data.idProperty().get(), image);
            } catch (Exception e) {
                data.exception.set(e);
            }
        };

        while (true) {
            try {
                imageCachingService.execute(command);
                break;
            } catch (RejectedExecutionException ignored) {
            }
            TimeUnit.MILLISECONDS.sleep(REJECTED_SLEEP_TIME);
        }

        return image;
    }

    @Override
    protected BufferedImage cropTitle(BufferedImage image) {
        return collectionArea.getTitle(image);
    }

    @Override
    protected Void callTask() throws Exception {
        // check if the screen resolution is supported using whether an exception occurs
        collectionArea = createCollectionArea();

        // check the cache directory
        cacheRepository = new CacheRepository(cacheDirectory);
        cacheRepository.prepare();

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
