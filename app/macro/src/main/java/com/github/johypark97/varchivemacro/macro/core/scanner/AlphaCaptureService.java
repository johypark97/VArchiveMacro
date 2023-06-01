package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskStatus;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

class AlphaCaptureService implements CaptureService {
    private static final int REJECTED_SLEEP_TIME = 1000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ImageCachingService imageCachingService = new ImageCachingService();
    private final Robot robot;

    private final int captureDelay;
    private final int inputDuration;
    public Exception exception;

    public AlphaCaptureService(int captureDelay, int inputDuration) {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            // It will be thrown when the environment does not support a display, keyboard, and
            // mouse. (Normally never be thrown)
            throw new RuntimeException(e);
        }

        if (captureDelay < 0) {
            throw new IllegalArgumentException("captureDelay must be positive: " + captureDelay);
        }
        if (inputDuration < 0) {
            throw new IllegalArgumentException("inputDuration must be positive: " + inputDuration);
        }

        this.captureDelay = captureDelay;
        this.inputDuration = inputDuration;
    }

    @Override
    public void shutdownNow() {
        executor.shutdownNow();
    }

    @Override
    public void await() throws InterruptedException {
        if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
            throw new RuntimeException("unexpected timeout");
        }
    }

    @Override
    public void execute(TaskManager taskManager, Map<String, List<LocalSong>> tabSongMap) {
        executor.execute(() -> {
            try {
                // Check if the screen size is supported using whether an exception occurs.
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                CollectionArea collectionArea = CollectionAreaFactory.create(screenSize);
                Rectangle screenRect = new Rectangle(screenSize);

                Queue<List<LocalSong>> tabQueue = new LinkedList<>(tabSongMap.values());
                try {
                    while (tabQueue.peek() != null) {
                        List<LocalSong> songList = tabQueue.poll();
                        int songListCount = songList.size();

                        tabKey(robot, KeyEvent.VK_SPACE, inputDuration);
                        for (int i = 0; i < songListCount; ++i) {
                            if (i != 0) {
                                tabKey(robot, KeyEvent.VK_DOWN, inputDuration);
                            }
                            TimeUnit.MILLISECONDS.sleep(captureDelay);

                            BufferedImage image = robot.createScreenCapture(screenRect);

                            LocalSong song = songList.get(i);
                            TaskData task = taskManager.createTask(song, collectionArea);
                            task.setSongIndex(i);
                            task.setSongCount(songListCount);
                            task.setStatus(TaskStatus.CAPTURED);
                            task.setValid(true);

                            while (true) {
                                try {
                                    imageCachingService.execute(task, image);
                                    break;
                                } catch (RejectedExecutionException ignored) {
                                }
                                TimeUnit.MILLISECONDS.sleep(REJECTED_SLEEP_TIME);
                            }
                        }
                    }
                } catch (InterruptedException ignored) {
                }

                // shutting down the image caching service
                imageCachingService.shutdown();

                // return to the all tab
                int count = tabQueue.size() + 2;
                for (int i = 0; i < count; ++i) {
                    tabKey(robot, KeyEvent.VK_SPACE, inputDuration);
                }

                // wait for the imageCachingService to shut down
                imageCachingService.await();
            } catch (InterruptedException e) {
                imageCachingService.shutdownNow();
            } catch (Exception e) {
                imageCachingService.shutdownNow();
                exception = e;
            }
        });
        executor.shutdown();
    }

    @Override
    public boolean hasException() {
        return exception != null;
    }

    @Override
    public Exception getException() {
        return exception;
    }
}
