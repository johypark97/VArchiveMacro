package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CaptureTask.Status;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection.CollectionAreaFactory;
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
import java.util.function.Function;

class CaptureService {
    private static final int INPUT_DURATION = 20;
    private static final int REJECTED_SLEEP_TIME = 1000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ImageCachingService imageCachingService = new ImageCachingService();
    private final Robot robot;

    private final Function<LocalSong, CaptureTask> taskCreator;
    public Exception exception;

    public CaptureService(Function<LocalSong, CaptureTask> taskCreator) {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            // It will be thrown when the environment does not support a display, keyboard, and
            // mouse. (Normally never be thrown)
            throw new RuntimeException(e);
        }

        this.taskCreator = taskCreator;
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

    public void execute(Map<String, List<LocalSong>> tabSongMap) {
        executor.execute(() -> {
            try {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                // Check if the screen size is supported using whether an exception occurs.
                CollectionAreaFactory.create(screenSize);

                Rectangle screenRect = new Rectangle(screenSize);
                for (List<LocalSong> songs : tabSongMap.values()) {
                    tabKey(KeyEvent.VK_SPACE);

                    boolean isFirst = true;
                    for (LocalSong song : songs) {
                        if (isFirst) {
                            isFirst = false;
                        } else {
                            tabKey(KeyEvent.VK_DOWN);
                        }

                        BufferedImage image = robot.createScreenCapture(screenRect);

                        CaptureTask task = taskCreator.apply(song);
                        task.setStatus(Status.CAPTURED);

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
            TimeUnit.MILLISECONDS.sleep(INPUT_DURATION);
        } finally {
            robot.keyRelease(keycode);
        }
        TimeUnit.MILLISECONDS.sleep(INPUT_DURATION);
    }
}
