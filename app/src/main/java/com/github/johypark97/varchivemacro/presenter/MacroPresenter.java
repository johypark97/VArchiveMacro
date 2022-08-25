package com.github.johypark97.varchivemacro.presenter;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import com.github.johypark97.varchivemacro.config.ConfigManager;
import com.github.johypark97.varchivemacro.hook.HookManager;
import com.github.johypark97.varchivemacro.model.SettingsModel;
import com.github.johypark97.varchivemacro.model.datastruct.SettingsData;
import com.github.johypark97.varchivemacro.presenter.converter.AnalyzeKeyConverter;
import com.github.johypark97.varchivemacro.presenter.converter.DirectionKeyConverter;
import com.github.johypark97.varchivemacro.resource.Language;
import com.github.johypark97.varchivemacro.view.LicenseView;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.google.gson.JsonSyntaxException;

public class MacroPresenter implements IMacro.Presenter {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private ConfigManager config = ConfigManager.getInstance();
    private MacroRobot robot = new MacroRobot(this);
    protected Language lang = Language.getInstance();

    // model
    public SettingsModel settingsModel;

    // view
    public IMacro.View macroView;
    public LicenseView licenseView;

    public void prepareView() {
        SettingsData data = new SettingsData();
        macroView.setSliderDefault(data.count, data.movingDelay, data.captureDuration,
                data.inputDuration);
        updateView(data);
    }

    private void updateView(SettingsData data) {
        macroView.setValues(data.count, data.movingDelay, data.captureDuration, data.inputDuration,
                AnalyzeKeyConverter.data2view(data.analyzeKey),
                DirectionKeyConverter.data2view(data.directionKey));
    }

    private SettingsData getSettingsDataFromView() {
        SettingsData data = new SettingsData();
        data.analyzeKey = AnalyzeKeyConverter.view2data(macroView.getAnalyzeKey());
        data.captureDuration = macroView.getCaptureDuration();
        data.count = macroView.getCount();
        data.directionKey = DirectionKeyConverter.view2data(macroView.getDirectionKey());
        data.inputDuration = macroView.getInputDuration();
        data.movingDelay = macroView.getMovingDelay();
        return data;
    }

    private void enableKeyListener() {
        HookManager.addKeyListener(new MacroNativeKeyListener(this));
    }

    public void selectUp() {
        macroView.setDirectionKey(IMacro.View.DirectionKey.UP);
    }

    public void selectDown() {
        macroView.setDirectionKey(IMacro.View.DirectionKey.DOWN);
    }

    public void startCapture() {
        try {
            robot.capture(getSettingsDataFromView());
        } catch (AWTException e) {
            macroView.showDialog(lang.get("p.macro_dialog.title"),
                    new String[] {lang.get("p.macro_dialog.msg"), lang.get("p.macro_dialog.msg2")},
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public void stopCapture() {
        robot.stop();
    }

    @Override
    public void addLog(String message) {
        String time = LocalTime.now().format(TIME_FORMATTER);

        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(time);
        builder.append("] ");
        builder.append(message);

        macroView.addLog(builder.toString());
    }

    @Override
    public void showLicense() {
        licenseView.showView();
    }

    @Override
    public void viewOpened() {
        HookManager.disableLogging();
        HookManager.setSwingEventDispatcher();
        try {
            HookManager.register();
            enableKeyListener();

            if (config.isConfigExists()) {
                config.load();
                updateView(settingsModel.getData());
            }
        } catch (NativeHookException e) {
            macroView.showDialog(lang.get("p.hook_dialog.title"),
                    new String[] {lang.get("p.hook_dialog.msg"), lang.get("p.hook_dialog.msg2")},
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            macroView
                    .showDialog(lang.get("p.config.read_dialog.title"),
                            new String[] {lang.get("p.config.read_dialog.error_msg"),
                                    lang.get("p.config.read_dialog.default")},
                            JOptionPane.ERROR_MESSAGE);
        } catch (JsonSyntaxException e) {
            macroView
                    .showDialog(lang.get("p.config.read_dialog.title"),
                            new String[] {lang.get("p.config.read_dialog.syntax_msg"),
                                    lang.get("p.config.read_dialog.default")},
                            JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void viewClosed() {
        robot.forceStop();
        settingsModel.setData(getSettingsDataFromView());
        try {
            config.save();
            HookManager.unregister();
        } catch (IOException e) {
            macroView.showDialog(lang.get("p.config.write_dialog.title"),
                    new String[] {lang.get("p.config.write_dialog.msg")},
                    JOptionPane.ERROR_MESSAGE);
        } catch (NativeHookException e) {
        }
    }
}


class MacroNativeKeyListener implements NativeKeyListener {
    private MacroPresenter presenter;

    public MacroNativeKeyListener(MacroPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_END:
                presenter.stopCapture();
                break;
            default:
                break;
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_UP:
                if ((e.getModifiers() & NativeInputEvent.CTRL_MASK) != 0)
                    presenter.selectUp();
                break;
            case NativeKeyEvent.VC_DOWN:
                if ((e.getModifiers() & NativeInputEvent.CTRL_MASK) != 0)
                    presenter.selectDown();
                break;
            case NativeKeyEvent.VC_HOME:
                if (e.getModifiers() == 0)
                    presenter.startCapture();
                break;
            default:
                break;
        }
    }
}


class MacroRobot {
    private MacroPresenter presenter;
    private Thread thread;

    public MacroRobot(MacroPresenter presenter) {
        this.presenter = presenter;
    }

    public synchronized void capture(SettingsData settingsData) throws AWTException {
        if (thread != null) {
            presenter.addLog(presenter.lang.get("p.capture_is_running"));
            return;
        }

        thread = new Thread(new MacroRobotRunner(presenter, settingsData, (isDone) -> {
            synchronized (this) {
                thread = null;
            }

            if (isDone)
                presenter.addLog(presenter.lang.get("p.capture_is_done"));
        }));

        presenter.addLog(
                String.format(presenter.lang.get("p.capture_has_started"), settingsData.count));
        thread.start();
    }

    public synchronized void stop() {
        if (thread == null) {
            presenter.addLog(presenter.lang.get("p.capture_is_not_running"));
            return;
        }

        thread.interrupt();
        presenter.addLog(presenter.lang.get("p.capture_stopped"));
    }

    public synchronized void forceStop() {
        if (thread != null)
            thread.interrupt();
    }
}


class MacroRobotRunner implements Runnable {
    private Consumer<Boolean> onReturn;
    private MacroPresenter presenter;
    private Robot robot;
    private SettingsData settingsData;
    private int analyzeKeyCode;
    private int directionKeyCode;

    public MacroRobotRunner(MacroPresenter presenter, SettingsData settingsData,
            Consumer<Boolean> onReturn) throws AWTException {
        this.onReturn = onReturn;
        this.presenter = presenter;
        this.settingsData = settingsData;
        robot = new Robot();

        getKeyCode();
    }

    private void getKeyCode() {
        analyzeKeyCode = switch (settingsData.analyzeKey) {
            case ALT_F11 -> KeyEvent.VK_F11;
            case ALT_F12 -> KeyEvent.VK_F12;
            case ALT_HOME -> KeyEvent.VK_HOME;
            case ALT_INS -> KeyEvent.VK_INSERT;
            default -> throw new RuntimeException("unknown analyze key");
        };

        directionKeyCode = switch (settingsData.directionKey) {
            case DOWN -> KeyEvent.VK_DOWN;
            case UP -> KeyEvent.VK_UP;
            default -> throw new RuntimeException("unknown direction key");
        };
    }

    @Override
    public void run() {
        if (settingsData.count > 0) {
            int movingDelay = settingsData.movingDelay - (settingsData.inputDuration << 2);

            try {
                int i = 1;
                while (true) {
                    pressKeyWithMod(KeyEvent.VK_ALT, KeyEvent.VK_PRINTSCREEN,
                            settingsData.captureDuration);

                    if (i < settingsData.count)
                        pressKey(directionKeyCode, settingsData.inputDuration);

                    pressKeyWithMod(KeyEvent.VK_ALT, analyzeKeyCode, settingsData.inputDuration);
                    presenter.addLog(String.format("(%d/%d)", i, settingsData.count));

                    if (i >= settingsData.count)
                        break;

                    ++i;
                    if (movingDelay > 0)
                        Thread.sleep(movingDelay);
                }
            } catch (InterruptedException e) {
                onReturn.accept(false);
                return;
            }
        }

        onReturn.accept(true);
    }

    private void pressKey(int keycode, int duration) throws InterruptedException {
        try {
            robot.keyPress(keycode);
            Thread.sleep(duration);
        } finally {
            robot.keyRelease(keycode);
        }
        Thread.sleep(duration);
    }

    private void pressKeyWithMod(int modcode, int keycode, int duration)
            throws InterruptedException {
        try {
            robot.keyPress(modcode);
            robot.keyPress(keycode);
            Thread.sleep(duration);
        } finally {
            robot.keyRelease(keycode);
            robot.keyRelease(modcode);
        }
        Thread.sleep(duration);
    }
}
