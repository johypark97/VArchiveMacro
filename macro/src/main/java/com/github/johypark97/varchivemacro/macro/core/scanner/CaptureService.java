package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager;
import java.awt.Robot;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface CaptureService {
    void shutdownNow();

    void await() throws InterruptedException;

    void execute(TaskManager taskManager, Map<String, List<LocalDlcSong>> tabSongMap);

    boolean hasException();

    Exception getException();

    default void tabKey(Robot robot, int keycode, long timeout) throws InterruptedException {
        boolean interrupted = false;

        robot.keyPress(keycode);
        try {
            sleep(timeout);
        } catch (InterruptedException e) {
            interrupted = true;
        }

        robot.keyRelease(keycode);
        try {
            sleep(timeout);
        } catch (InterruptedException e) {
            interrupted = true;
        }

        if (interrupted) {
            throw new InterruptedException();
        }
    }

    default void sleep(long timeout) throws InterruptedException {
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
}
