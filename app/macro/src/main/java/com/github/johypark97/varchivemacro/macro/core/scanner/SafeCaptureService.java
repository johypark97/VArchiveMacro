package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.image.ImageConverter;
import com.github.johypark97.varchivemacro.macro.core.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.macro.core.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskStatus;
import com.google.common.base.CharMatcher;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class SafeCaptureService implements CaptureService {
    private static final String QUOTE_UNICODE = "\u2019";
    private static final int REJECTED_SLEEP_TIME = 1000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ImageCachingService imageCachingService = new ImageCachingService();
    private final Robot robot;

    private final int captureDelay;
    private final int inputDuration;
    public Exception exception;

    public SafeCaptureService(int captureDelay, int inputDuration) {
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

    private static String normalizeTitle(String value) {
        String s = value.toLowerCase(Locale.ENGLISH);
        s = CharMatcher.whitespace().removeFrom(s);
        s = CharMatcher.forPredicate(SafeCaptureService::asciiFilter).removeFrom(s);
        s = CharMatcher.anyOf(QUOTE_UNICODE).removeFrom(s);
        s = CharMatcher.anyOf("ILil").replaceFrom(s, '!');
        return s;
    }

    private static boolean asciiFilter(Character value) {
        int ASCII_MAX = 0x7F;
        int SPACE_CHAR = 0x20;

        boolean p = false;

        if (value > ASCII_MAX) {
            p = true;
        } else {
            if (value == SPACE_CHAR) {
                p = true;
            } else if (value >= 0x30 && value <= 0x39) { // digit
                p = true;
            } else if (value >= 0x41 && value <= 0x5A) { // uppercase alphabet
                p = true;
            } else if (value >= 0x61 && value <= 0x7A) { // lowercase alphabet
                p = true;
            }
        }

        return !p;
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
            try (OcrWrapper ocr = new OcrWrapper()) {
                ocr.setWhitelist(
                        " !#$%&'(),-./012345789:?ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~"
                                + QUOTE_UNICODE);

                // Check if the screen size is supported using whether an exception occurs.
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                CollectionArea collectionArea = CollectionAreaFactory.create(screenSize);
                Rectangle screenRect = new Rectangle(screenSize);

                Queue<List<LocalSong>> tabQueue = new LinkedList<>(tabSongMap.values());
                try {
                    while (tabQueue.peek() != null) {
                        List<LocalSong> songList = tabQueue.poll();
                        int songListCount = songList.size();

                        Map<String, Queue<TaskData>> queueMap = new HashMap<>();
                        for (int i = 0; i < songListCount; ++i) {
                            LocalSong song = songList.get(i);

                            TaskData task = taskManager.createTask(song, collectionArea);
                            task.setSongIndex(i);
                            task.setSongCount(-songListCount);

                            String title = normalizeTitle(song.title());
                            queueMap.computeIfAbsent(title, (x) -> new LinkedList<>()).add(task);
                        }
                        queueMap.values().removeIf((x) -> x.size() > 1);

                        tabKey(robot, KeyEvent.VK_SPACE, inputDuration);
                        for (int i = 0; i < songListCount; ++i) {
                            if (i != 0) {
                                tabKey(robot, KeyEvent.VK_DOWN, inputDuration);
                            }
                            TimeUnit.MILLISECONDS.sleep(captureDelay);

                            // capture
                            BufferedImage image = robot.createScreenCapture(screenRect);

                            // analyze title image
                            String title;
                            byte[] imageBytes =
                                    ImageConverter.imageToPngBytes(collectionArea.getTitle(image));
                            try (PixWrapper pix = new PixWrapper(imageBytes)) {
                                PixPreprocessor.preprocessTitle(pix);
                                title = ocr.run(pix.pixInstance).trim();
                            }
                            String nTitle = normalizeTitle(title);

                            // find song
                            TaskData task =
                                    queueMap.getOrDefault(nTitle, new LinkedList<>()).poll();
                            if (task != null) {
                                task.setScannedTitle(String.format("%s [%s]", title, nTitle));
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

                        /*
                        // -------- for test --------
                        Collections.shuffle(songList);
                        for (int i = 0; i < songListCount; ++i) {
                            // pick a random song for test
                            LocalSong randomSong = songList.get(i);
                            String randomSongTitle = normalizeTitle(randomSong.title());

                            // load image
                            BufferedImage image =
                                    taskManager.getImageCacheManager().loadImage(randomSong);

                            // analyze title image
                            String title;
                            byte[] bytes =
                                    ImageConverter.imageToPngBytes(collectionArea.getTitle(image));
                            try (PixWrapper pix = new PixWrapper(bytes)) {
                                PixPreprocessor.preprocessTitle(pix);
                                title = ocr.run(pix.pixInstance).trim();
                            }
                            String nTitle = normalizeTitle(title);

                            // check whether it finds the right song
                            TaskData task =
                                    queueMap.getOrDefault(nTitle, new LinkedList<>()).poll();
                            if (task != null) {
                                task.setScannedTitle(String.format("%s [%s] <-> %s", title, nTitle,
                                        randomSongTitle));
                                task.setValid(nTitle.equals(randomSongTitle));
                            } else {
                                task = taskManager.createTask(randomSong, collectionArea);
                                task.setScannedTitle(
                                        String.format("NOT FOUNDED: %s [%s] <-> %s", title, nTitle,
                                                randomSongTitle));
                            }

                            if (Thread.interrupted()) {
                                throw new InterruptedException();
                            }
                        }
                        // -------- end for test --------
                        */
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
