package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.util.CacheHelper;
import com.github.johypark97.varchivemacro.lib.common.AwtRobotHelper;
import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;

public class OcrCacheCaptureTask extends Task<Void> {
    private static final Path ABSOLUTE_PATH = Path.of("").toAbsolutePath();
    private static final String CACHE_OVERLAY_FILENAME = "cache_overlay";
    private static final String CACHE_OVERLAY_FORMAT = "png";
    private static final String IMAGE_DIRECTORY_NAME = "images";

    public static final int CAPTURE_DELAY_DEFAULT = 50;
    public static final int CAPTURE_DELAY_MAX = 1000;
    public static final int CAPTURE_DELAY_MIN = 0;

    public static final int KEY_INPUT_DELAY_DEFAULT = 50;
    public static final int KEY_INPUT_DELAY_MAX = 500;
    public static final int KEY_INPUT_DELAY_MIN = 0;

    public static final int KEY_INPUT_DURATION_DEFAULT = 50;
    public static final int KEY_INPUT_DURATION_MAX = 200;
    public static final int KEY_INPUT_DURATION_MIN = 20;

    private final Robot robot = new Robot();

    public List<LocalDlcSong> dlcSongList;
    public Path outputPath;
    public int captureDelay;
    public int keyInputDelay;
    public int keyInputDuration;

    public OcrCacheCaptureTask() throws AWTException {
    }

    private void checkRange(int value, int min, int max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(message);
        }
    }

    private void tabKey(int keyCode, int... modifier) throws InterruptedException {
        AwtRobotHelper.sleepLeast(keyInputDelay);
        AwtRobotHelper.tabKey(robot, keyInputDuration, keyCode, modifier);
    }

    private BufferedImage captureScreenshot() throws InterruptedException {
        AwtRobotHelper.sleepLeast(captureDelay);
        return AwtRobotHelper.captureScreenshot(robot);
    }

    @Override
    protected Void call() throws Exception {
        Objects.requireNonNull(dlcSongList);
        Objects.requireNonNull(outputPath);

        checkRange(captureDelay, CAPTURE_DELAY_MIN, CAPTURE_DELAY_MAX, "captureDelay");
        checkRange(keyInputDelay, KEY_INPUT_DELAY_MIN, KEY_INPUT_DELAY_MAX, "keyInputDelay");
        checkRange(keyInputDuration, KEY_INPUT_DURATION_MIN, KEY_INPUT_DURATION_MAX,
                "keyInputDuration");

        if (!outputPath.isAbsolute()) {
            outputPath = outputPath.toAbsolutePath();
        }

        if (!new PathHelper(outputPath).isSubPathOf(ABSOLUTE_PATH)) {
            throw new RuntimeException("outputPath is not a subdirectory of the program root.");
        }

        CollectionArea area;
        BufferedImage overlayImage;

        {
            BufferedImage image = captureScreenshot();

            Dimension screenSize = new Dimension(image.getWidth(), image.getHeight());
            area = CollectionAreaFactory.create(screenSize);

            Dimension titleSize = area.getTitle_training().getSize();
            overlayImage = new BufferedImage(titleSize.width, titleSize.height,
                    BufferedImage.TYPE_INT_RGB);
        }

        CacheHelper.clearAndReadyDirectory(outputPath);

        Path imageOutputPath = outputPath.resolve(IMAGE_DIRECTORY_NAME);
        Files.createDirectory(imageOutputPath);

        final int count = dlcSongList.size();
        for (int i = 0; i < count; ++i) {
            LocalDlcSong song = dlcSongList.get(i);

            if (i != 0) {
                tabKey(KeyEvent.VK_DOWN);
            }

            BufferedImage screenshot = captureScreenshot();
            BufferedImage titleImage = area.getTitle_training(screenshot);

            try (PixWrapper pix = new PixWrapper(ImageConverter.imageToPngBytes(titleImage))) {
                PixPreprocessor.thresholdWhite(pix);
                BufferedImage preprocessed = ImageConverter.pngBytesToImage(pix.getPngBytes());

                for (int x = 0; x < preprocessed.getWidth(); ++x) {
                    for (int y = 0; y < preprocessed.getHeight(); ++y) {
                        Color c = new Color(preprocessed.getRGB(x, y));

                        if (Color.WHITE.equals(c)) {
                            overlayImage.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }
                }
            }

            Path path = CacheHelper.createImagePath(imageOutputPath, song);
            ImageIO.write(titleImage, CacheHelper.IMAGE_FORMAT, path.toFile());
        }

        Path overlayPath = outputPath.resolve(CACHE_OVERLAY_FILENAME + '.' + CACHE_OVERLAY_FORMAT);
        ImageIO.write(overlayImage, CACHE_OVERLAY_FORMAT, overlayPath.toFile());

        return null;
    }
}
