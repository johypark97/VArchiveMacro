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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;

public class CacheCaptureTask implements Callable<Void> {
    private final Robot robot = new Robot();
    private final Runnable whenContinuousCaptureDone;

    public CacheGeneratorConfig config;
    public List<LocalSong> songList;

    public CacheCaptureTask(Runnable whenContinuousCaptureDone) throws AWTException {
        this.whenContinuousCaptureDone = whenContinuousCaptureDone;
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

        CacheHelper.clearAndReadyDirectory(config.cacheDir);

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

                List<BufferedImage> imageList = new ArrayList<>();
                for (int j = 0; j < config.count; ++j) {
                    if (j != 0) {
                        Thread.sleep(config.continuousCaptureDelay);
                    }

                    BufferedImage image = robot.createScreenCapture(screenRect);
                    imageList.add(image);
                }

                whenContinuousCaptureDone.run();

                int imageNumber = -1;
                for (BufferedImage image : imageList) {
                    ++imageNumber;

                    Path path = CacheHelper.createImagePath(config.cacheDir, song, imageNumber);
                    ImageIO.write(image, CacheHelper.IMAGE_FORMAT, path.toFile());
                }
            }
        } catch (InterruptedException ignored) {
        }

        return null;
    }
}
