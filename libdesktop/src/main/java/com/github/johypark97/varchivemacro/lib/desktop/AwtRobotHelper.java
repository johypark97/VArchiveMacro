package com.github.johypark97.varchivemacro.lib.desktop;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AwtRobotHelper {
    public static void sleepLeast(long timeout) throws InterruptedException {
        boolean interrupted = false;

        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(timeout);
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        if (interrupted) {
            throw new InterruptedException();
        }
    }

    public static void tabKey(Robot robot, long duration, int keyCode, int... modifier)
            throws InterruptedException {
        boolean interrupted = false;

        Arrays.stream(modifier).forEach(robot::keyPress);
        robot.keyPress(keyCode);

        try {
            sleepLeast(duration);
        } catch (InterruptedException e) {
            interrupted = true;
        }

        robot.keyRelease(keyCode);
        Arrays.stream(modifier).forEach(robot::keyRelease);

        try {
            sleepLeast(duration);
        } catch (InterruptedException e) {
            interrupted = true;
        }

        if (interrupted) {
            throw new InterruptedException();
        }
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
}
