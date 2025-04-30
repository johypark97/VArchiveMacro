package com.github.johypark97.varchivemacro.macro.application.macro.task;

import com.github.johypark97.varchivemacro.lib.desktop.AwtRobotHelper;
import com.github.johypark97.varchivemacro.macro.application.common.InterruptibleTask;
import com.github.johypark97.varchivemacro.macro.application.macro.model.MacroDirection;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.UploadKey;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class MacroTask extends InterruptibleTask<Void> {
    private final MacroDirection direction;
    private final Robot robot;
    private final UploadKey uploadKey;
    private final int captureDelay;
    private final int captureDuration;
    private final int count;
    private final int keyInputDuration;

    public MacroTask(UploadKey uploadKey, int count, int captureDelay, int captureDuration,
            int keyInputDuration, MacroDirection direction) {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        this.captureDelay = captureDelay;
        this.captureDuration = captureDuration;
        this.count = count;
        this.direction = direction;
        this.keyInputDuration = keyInputDuration;
        this.uploadKey = uploadKey;
    }

    private void tabKey(int keyCode, int... modifier) throws InterruptedException {
        AwtRobotHelper.tabKey(robot, keyInputDuration, keyCode, modifier);
    }

    private void moveSong() throws InterruptedException {
        int keyCode = switch (direction) {
            case UP -> KeyEvent.VK_UP;
            case DOWN -> KeyEvent.VK_DOWN;
        };

        tabKey(keyCode);
    }

    private void capture() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(captureDelay);

        tabKey(KeyEvent.VK_PRINTSCREEN, KeyEvent.VK_ALT);

        TimeUnit.MILLISECONDS.sleep(captureDuration);
    }

    private void upload() throws InterruptedException {
        int keyCode = switch (uploadKey) {
            case F11 -> KeyEvent.VK_F11;
            case F12 -> KeyEvent.VK_F12;
            case HOME -> KeyEvent.VK_HOME;
            case INSERT -> KeyEvent.VK_INSERT;
        };

        tabKey(keyCode, KeyEvent.VK_ALT);
    }

    @Override
    protected Void callTask() throws Exception {
        try {
            for (int i = 0; i < count; ++i) {
                if (i != 0) {
                    moveSong();
                }

                capture();
                upload();
            }
        } catch (InterruptedException ignored) {
        }

        return null;
    }
}
