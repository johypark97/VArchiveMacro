package com.github.johypark97.varchivemacro.macro.core.clientmacro;

import com.github.johypark97.varchivemacro.macro.core.command.AbstractCommand;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ClientMacro {
    private final Consumer<Exception> whenThrown;
    private final Runnable whenCanceled;
    private final Runnable whenDone;
    private final Runnable whenStart;

    public ClientMacro(Consumer<Exception> whenThrown, Runnable whenCanceled, Runnable whenDone,
            Runnable whenStart) {
        this.whenCanceled = whenCanceled;
        this.whenDone = whenDone;
        this.whenStart = whenStart;
        this.whenThrown = whenThrown;
    }

    public Command createCommand(AnalyzeKey analyzeKey, Direction direction, int captureDelay,
            int captureDuration, int count, int keyInputDuration) {
        int analyzeKeycode = switch (analyzeKey) {
            case F11 -> KeyEvent.VK_F11;
            case F12 -> KeyEvent.VK_F12;
            case HOME -> KeyEvent.VK_HOME;
            case INSERT -> KeyEvent.VK_INSERT;
        };

        int moveKeycode = switch (direction) {
            case DOWN -> KeyEvent.VK_DOWN;
            case UP -> KeyEvent.VK_UP;
        };

        return new ClientMacroCommand(count, captureDelay, captureDuration, keyInputDuration,
                analyzeKeycode, moveKeycode);
    }


    private class ClientMacroCommand extends AbstractCommand {
        private final Robot robot;

        private final int analyzeKeycode;
        private final int captureDelay;
        private final int captureDuration;
        private final int count;
        private final int keyInputDuration;
        private final int moveKeycode;

        public ClientMacroCommand(int count, int captureDelay, int captureDuration,
                int keyInputDuration, int analyzeKeycode, int moveKeycode) {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }

            this.analyzeKeycode = analyzeKeycode;
            this.captureDelay = captureDelay;
            this.captureDuration = captureDuration;
            this.count = count;
            this.keyInputDuration = keyInputDuration;
            this.moveKeycode = moveKeycode;
        }

        private void tabKey(int keycode, int duration) throws InterruptedException {
            try {
                robot.keyPress(keycode);
                TimeUnit.MILLISECONDS.sleep(duration);
            } finally {
                robot.keyRelease(keycode);
            }
            TimeUnit.MILLISECONDS.sleep(duration);
        }

        private void tabKeyWithAlt(int keycode, int duration) throws InterruptedException {
            try {
                robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(keycode);
                TimeUnit.MILLISECONDS.sleep(duration);
            } finally {
                robot.keyRelease(keycode);
                robot.keyRelease(KeyEvent.VK_ALT);
            }
            TimeUnit.MILLISECONDS.sleep(duration);
        }

        @Override
        public boolean run() {
            whenStart.run();

            int waitTime = captureDelay - (captureDuration << 2);

            try {
                for (int i = 1; ; ++i) {
                    tabKeyWithAlt(KeyEvent.VK_PRINTSCREEN, captureDuration);

                    if (i < count) {
                        tabKey(moveKeycode, keyInputDuration);
                    }

                    tabKeyWithAlt(analyzeKeycode, keyInputDuration);

                    if (i >= count) {
                        break;
                    }

                    TimeUnit.MILLISECONDS.sleep(waitTime);
                }
            } catch (InterruptedException e) {
                whenCanceled.run();
                return false;
            } catch (Exception e) {
                whenThrown.accept(e);
                return false;
            }

            whenDone.run();
            return false;
        }
    }
}
