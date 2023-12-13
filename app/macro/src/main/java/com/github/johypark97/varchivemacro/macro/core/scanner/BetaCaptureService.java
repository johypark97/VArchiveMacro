package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.common.ImageConverter;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.lib.common.recognizer.TitleSongRecognizer;
import com.github.johypark97.varchivemacro.lib.common.recognizer.TitleSongRecognizer.Recognized;
import com.github.johypark97.varchivemacro.macro.core.TitleOcr;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskStatus;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class BetaCaptureService implements CaptureService {
    private static final String TAB_NAME_CLEAR_PASS_PLUS = "CLEARPASS+";
    private static final int DUPLICATED_CONDITION = 2;
    private static final int REJECTED_SLEEP_TIME = 1000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ImageCachingService imageCachingService = new ImageCachingService();
    private final Robot robot;

    private final TitleTool titleTool;
    private final int captureDelay;
    private final int inputDuration;
    public Exception exception;

    public BetaCaptureService(TitleTool titleTool, int captureDelay, int inputDuration) {
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
        this.titleTool = titleTool;
    }

    public static BufferedImage captureScreenshot(Robot robot) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        MultiResolutionImage multiResolutionImage =
                robot.createMultiResolutionScreenCapture(new Rectangle(screenSize));
        List<Image> variantList = multiResolutionImage.getResolutionVariants();
        Image image = variantList.get(variantList.size() - 1);

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        return bufferedImage;
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
    public void execute(TaskManager taskManager, Map<String, List<LocalDlcSong>> tabSongMap) {
        executor.execute(() -> {
            TitleSongRecognizer<LocalDlcSong> recognizer = new TitleSongRecognizer<>(titleTool);

            try (OcrWrapper ocr = new TitleOcr()) {
                CollectionArea collectionArea;
                {
                    Image screenImage = captureScreenshot(robot);
                    Dimension screenSize =
                            new Dimension(screenImage.getWidth(null), screenImage.getHeight(null));

                    // Check if the screen size is supported using whether an exception occurs.
                    collectionArea = CollectionAreaFactory.create(screenSize);
                }

                Queue<List<LocalDlcSong>> tabQueue = new LinkedList<>();
                for (Entry<String, List<LocalDlcSong>> entry : tabSongMap.entrySet()) {
                    // Add an empty list before ClearPass+ tab to skip the favorite tab.
                    if (TAB_NAME_CLEAR_PASS_PLUS.equals(entry.getKey())) {
                        tabQueue.add(List.of());
                    }

                    tabQueue.add(entry.getValue());
                }

                try {
                    while (tabQueue.peek() != null) {
                        List<LocalDlcSong> songList = tabQueue.poll();
                        recognizer.setSongList(songList);

                        tabKey(robot, KeyEvent.VK_SPACE, inputDuration);

                        if (songList.isEmpty()) {
                            continue;
                        }

                        LocalDlcSong previousRecognizedSong = null;
                        boolean isFirst = true;
                        int duplicatedCount = 0;
                        while (true) {
                            if (isFirst) {
                                isFirst = false;
                            } else {
                                tabKey(robot, KeyEvent.VK_DOWN, inputDuration);
                            }
                            TimeUnit.MILLISECONDS.sleep(captureDelay);

                            // capture
                            BufferedImage image = captureScreenshot(robot);

                            // analyze title image
                            byte[] imageBytes =
                                    ImageConverter.imageToPngBytes(collectionArea.getTitle(image));
                            String scannedTitle;
                            try (PixWrapper pix = new PixWrapper(imageBytes)) {
                                PixPreprocessor.preprocessTitle(pix);
                                scannedTitle = ocr.run(pix.pixInstance);
                            }

                            Recognized<LocalDlcSong> recognized =
                                    recognizer.recognize(scannedTitle);
                            if (Objects.equals(recognized.song(), previousRecognizedSong)) {
                                ++duplicatedCount;
                                if (duplicatedCount >= DUPLICATED_CONDITION) {
                                    break;
                                }
                            } else {
                                duplicatedCount = 0;

                                TaskData task = taskManager.createTask(collectionArea);
                                task.setScannedTitle(recognized.normalizedInput());

                                switch (recognized.status()) {
                                    case DUPLICATED_SONG -> task.setStatus(TaskStatus.DUPLICATED);
                                    case FOUND -> {
                                        task.setAccuracy(recognized.similarity());
                                        task.setDistance(recognized.distance());
                                        task.setSelected(recognized.distance() == 0);
                                        task.setSong(recognized.song());
                                        task.setStatus(TaskStatus.FOUND);
                                    }
                                    case NOT_FOUND -> task.setStatus(TaskStatus.NOT_FOUND);
                                }

                                while (true) {
                                    try {
                                        imageCachingService.execute(task, image);
                                        break;
                                    } catch (RejectedExecutionException ignored) {
                                    }
                                    TimeUnit.MILLISECONDS.sleep(REJECTED_SLEEP_TIME);
                                }
                            }

                            previousRecognizedSong = recognized.song();
                        }
                    }
                } catch (InterruptedException ignored) {
                }

                // shutting down the image caching service
                imageCachingService.shutdown();

                // return to the all tab
                int count = tabQueue.size() + 1;
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
