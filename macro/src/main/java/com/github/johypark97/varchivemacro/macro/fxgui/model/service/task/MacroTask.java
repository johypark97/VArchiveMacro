package com.github.johypark97.varchivemacro.macro.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.lib.desktop.AwtRobotHelper;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel.AnalysisKey;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import javafx.geometry.VerticalDirection;

public final class MacroTask extends InterruptibleTask<Void> {
    private final AnalysisKey analysisKey;
    private final Robot robot;
    private final VerticalDirection direction;
    private final int captureDelay;
    private final int captureDuration;
    private final int count;
    private final int keyInputDuration;

    public MacroTask(AnalysisKey analysisKey, int count, int captureDelay, int captureDuration,
            int keyInputDuration, VerticalDirection direction) {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        this.analysisKey = analysisKey;
        this.captureDelay = captureDelay;
        this.captureDuration = captureDuration;
        this.count = count;
        this.direction = direction;
        this.keyInputDuration = keyInputDuration;
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

    private void analyze() throws InterruptedException {
        int keyCode = switch (analysisKey) {
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
                analyze();
            }
        } catch (InterruptedException ignored) {
        }

        return null;
    }
}
