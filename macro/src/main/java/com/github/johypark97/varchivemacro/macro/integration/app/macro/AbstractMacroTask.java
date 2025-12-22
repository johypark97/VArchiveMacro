package com.github.johypark97.varchivemacro.macro.integration.app.macro;

import com.github.johypark97.varchivemacro.lib.desktop.AwtRobotHelper;
import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.github.johypark97.varchivemacro.macro.common.config.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.integration.app.common.InterruptibleTask;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMacroTask extends InterruptibleTask<MacroProgress> {
    private final Robot robot;

    private final int count;
    private final int keyHoldTime;
    private final int postCaptureDelay;
    private final int songSwitchingTime;

    private final int nextSongKey_keyCode;

    private final int uploadKey_keyCode;
    private final int[] uploadKey_modifier;

    public AbstractMacroTask(MacroConfig config, MacroDirection direction) {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        count = config.count().value();
        keyHoldTime = config.keyHoldTime().value();
        postCaptureDelay = config.postCaptureDelay().value();
        songSwitchingTime = config.songSwitchingTime().value();

        nextSongKey_keyCode = switch (direction) {
            case UP -> InputKey.UP.toAwtKeyCode();
            case DOWN -> InputKey.DOWN.toAwtKeyCode();
        };

        uploadKey_keyCode = config.uploadKey().value().key().toAwtKeyCode();
        uploadKey_modifier = config.uploadKey().value().modifierKeyCodeArray();
    }

    protected abstract void runMacro_forSong() throws InterruptedException;

    protected final void nextSong() throws InterruptedException {
        AwtRobotHelper.tabKey(robot, keyHoldTime, nextSongKey_keyCode);
    }

    protected final void capture() throws InterruptedException {
        AwtRobotHelper.tabKey(robot, keyHoldTime, InputKey.PRINTSCREEN.toAwtKeyCode(),
                InputKey.ALT.toAwtKeyCode());
    }

    protected final void upload() throws InterruptedException {
        AwtRobotHelper.tabKey(robot, keyHoldTime, uploadKey_keyCode, uploadKey_modifier);
    }

    protected final void sleep_postCaptureDelay() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(postCaptureDelay);
    }

    protected final void sleep_songSwitchingTime() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(songSwitchingTime);
    }

    @Override
    protected final MacroProgress callTask() throws Exception {
        updateValue(new MacroProgress(0, count));

        try {
            TimeUnit.MILLISECONDS.sleep(500);

            for (int i = 1; i <= count; i++) {
                if (i > 1) {
                    nextSong();
                    sleep_songSwitchingTime();
                }

                runMacro_forSong();

                updateValue(new MacroProgress(i, count));
            }
        } catch (InterruptedException ignored) {
        }

        return new MacroProgress(count, count);
    }
}
