package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.util.CacheHelper;
import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.desktop.AwtRobotHelper;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;

public class OcrCacheCaptureTask extends Task<Void> {
    private static final Path ABSOLUTE_PATH = Path.of("").toAbsolutePath();

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

    public List<Song> songList;
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
        Objects.requireNonNull(outputPath);
        Objects.requireNonNull(songList);

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

        CacheHelper.clearAndReadyDirectory(outputPath);

        final int count = songList.size();
        for (int i = 0; i < count; ++i) {
            if (i != 0) {
                tabKey(KeyEvent.VK_DOWN);
            }

            BufferedImage screenshot = captureScreenshot();

            Song song = songList.get(i);
            Path path = CacheHelper.createImagePath(outputPath, song.id());
            ImageIO.write(screenshot, CacheHelper.IMAGE_FORMAT, path.toFile());
        }

        return null;
    }
}
