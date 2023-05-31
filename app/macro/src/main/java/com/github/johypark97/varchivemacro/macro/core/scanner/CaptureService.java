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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

class CaptureService {
    private static final int REJECTED_SLEEP_TIME = 1000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ImageCachingService imageCachingService = new ImageCachingService();
    private final Robot robot;

    private final int captureDelay;
    private final int inputDuration;
    public Exception exception;

    public CaptureService(int captureDelay, int inputDuration) {
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

    public void shutdownNow() {
        executor.shutdownNow();

        imageCachingService.shutdownNow();
    }

    public void await() throws InterruptedException {
        awaitCapture();
        imageCachingService.await();
    }

    public void awaitCapture() throws InterruptedException {
        if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
            throw new RuntimeException("unexpected timeout");
        }
    }

    public void execute(TaskManager taskManager, Map<String, List<LocalSong>> tabSongMap) {
        executor.execute(() -> {
            try {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                // Check if the screen size is supported using whether an exception occurs.
                CollectionArea area = CollectionAreaFactory.create(screenSize);

                Rectangle screenRect = new Rectangle(screenSize);
                for (List<LocalSong> songs : tabSongMap.values()) {
                    tabKey(KeyEvent.VK_SPACE);

                    int count = songs.size();
                    for (int i = 0; i < count; ++i) {
                        if (i != 0) {
                            tabKey(KeyEvent.VK_DOWN);
                        }

                        TimeUnit.MILLISECONDS.sleep(captureDelay);

                        BufferedImage image = robot.createScreenCapture(screenRect);

                        LocalSong song = songs.get(i);
                        TaskData task = taskManager.createTask(song, area);
                        task.setSongIndex(i);
                        task.setSongCount(count);
                        task.setStatus(TaskStatus.CAPTURED);

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

                imageCachingService.shutdown();
            } catch (InterruptedException ignored) {
                imageCachingService.shutdownNow();
            } catch (Exception e) {
                imageCachingService.shutdownNow();
                exception = e;
            }
        });
        executor.shutdown();
    }

    private void tabKey(int keycode) throws InterruptedException {
        try {
            robot.keyPress(keycode);
            TimeUnit.MILLISECONDS.sleep(inputDuration);
        } finally {
            robot.keyRelease(keycode);
        }
        TimeUnit.MILLISECONDS.sleep(inputDuration);
    }
}
