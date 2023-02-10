package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScanData.Status;
import java.awt.AWTException;
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
    private final ImageWritingService imageWritingService = new ImageWritingService();
    private final Robot robot;

    public Exception exception;

    public CaptureService() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            // It will be thrown when the environment does not support a display, keyboard, and
            // mouse. (Normally never be thrown)
            throw new RuntimeException(e);
        }
    }

    public void shutdownNow() {
        executor.shutdownNow();

        imageWritingService.shutdownNow();
    }

    public void await() throws InterruptedException {
        awaitCapture();
        imageWritingService.await();
    }

    public void awaitCapture() throws InterruptedException {
        if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
            throw new RuntimeException("unexpected timeout");
        }
    }

    public void execute(Map<String, List<LocalSong>> tabSongMap,
            Function<LocalSong, ScanData> dataCreator) {
        executor.execute(() -> {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            try {
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

                        ScanData data = dataCreator.apply(song);
                        data.setStatus(Status.CAPTURED);

                        while (true) {
                            try {
                                imageWritingService.execute(data, image);
                                break;
                            } catch (RejectedExecutionException ignored) {
                            }
                            TimeUnit.MILLISECONDS.sleep(REJECTED_SLEEP_TIME);
                        }
                    }
                }
                imageWritingService.shutdown();
            } catch (Exception e) {
                imageWritingService.shutdownNow();
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
