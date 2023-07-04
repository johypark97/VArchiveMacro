package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.CacheGeneratorConfig;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

public class CacheCaptureTask implements Callable<Void> {
    private final Robot robot = new Robot();

    public CacheGeneratorConfig config;
    public List<LocalSong> songList;

    public CacheCaptureTask() throws AWTException {
    }

    public void setConfig(CacheGeneratorConfig config) {
        this.config = config;
    }

    public void setSongList(List<LocalSong> songList) {
        this.songList = songList;
    }

    private void tabKey(int keycode, long time) throws InterruptedException {
        robot.keyPress(keycode);
        Thread.sleep(time);
        robot.keyRelease(keycode);
        Thread.sleep(time);
    }

    @Override
    public Void call() throws Exception {
        Objects.requireNonNull(config);
        Objects.requireNonNull(songList);

        if (!Files.exists(config.cacheDir)) {
            Files.createDirectories(config.cacheDir);
        } else if (!Files.isDirectory(config.cacheDir)) {
            throw new RuntimeException("path is a file");
        } else {
            List<Path> list;
            try (Stream<Path> stream = Files.walk(config.cacheDir)) {
                list = stream.sorted(Comparator.reverseOrder()).toList();
            }

            Path absDir = config.cacheDir.toAbsolutePath();
            for (Path path : list) {
                if (!absDir.equals(path.toAbsolutePath())) {
                    Files.delete(path);
                }
            }
        }

        int count = songList.size();
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRect = new Rectangle(screenSize);

            for (int i = 0; i < count; ++i) {
                LocalSong song = songList.get(i);

                if (i != 0) {
                    tabKey(KeyEvent.VK_DOWN, config.inputDuration);
                }

                Thread.sleep(config.captureDelay);
                BufferedImage image = robot.createScreenCapture(screenRect);

                Path path = CacheHelper.createPath(config.cacheDir, song);
                ImageIO.write(image, CacheHelper.FORMAT, path.toFile());
            }
        } catch (InterruptedException ignored) {
        }

        return null;
    }
}
