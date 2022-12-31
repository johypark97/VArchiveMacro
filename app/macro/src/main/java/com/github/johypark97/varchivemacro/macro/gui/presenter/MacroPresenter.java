package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.lib.hook.HookWrapper;
import com.github.johypark97.varchivemacro.macro.config.ConfigManager;
import com.github.johypark97.varchivemacro.macro.gui.model.SettingsModel;
import com.github.johypark97.varchivemacro.macro.gui.model.datastruct.SettingsData;
import com.github.johypark97.varchivemacro.macro.gui.presenter.converter.AnalyzeKeyConverter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.converter.DirectionKeyConverter;
import com.github.johypark97.varchivemacro.macro.util.Language;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.google.gson.JsonSyntaxException;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import javax.swing.JOptionPane;

public class MacroPresenter implements IMacro.Presenter {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // model
    public SettingsModel settingsModel;

    // view
    public final IMacro.View view;

    // other presenters
    public ILicense.Presenter licensePresenter;

    private final ConfigManager config = ConfigManager.getInstance();
    private final MacroRobot robot = new MacroRobot(this);
    protected Language lang = Language.getInstance();

    public MacroPresenter(IMacro.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    private void prepareView() {
        SettingsData data = new SettingsData();
        view.setSliderDefault(data.count, data.movingDelay, data.captureDuration,
                data.inputDuration);
        updateView(data);
    }

    private void updateView(SettingsData data) {
        view.setValues(data.count, data.movingDelay, data.captureDuration, data.inputDuration,
                AnalyzeKeyConverter.data2view(data.analyzeKey),
                DirectionKeyConverter.data2view(data.directionKey));
    }

    private SettingsData getSettingsDataFromView() {
        SettingsData data = new SettingsData();
        data.analyzeKey = AnalyzeKeyConverter.view2data(view.getAnalyzeKey());
        data.captureDuration = view.getCaptureDuration();
        data.count = view.getCount();
        data.directionKey = DirectionKeyConverter.view2data(view.getDirectionKey());
        data.inputDuration = view.getInputDuration();
        data.movingDelay = view.getMovingDelay();
        return data;
    }

    private void enableKeyListener() {
        HookWrapper.addKeyListener(new MacroNativeKeyListener(this));
    }

    public void selectUp() {
        view.setDirectionKey(IMacro.View.DirectionKey.UP);
    }

    public void selectDown() {
        view.setDirectionKey(IMacro.View.DirectionKey.DOWN);
    }

    public void startCapture() {
        try {
            robot.capture(getSettingsDataFromView());
        } catch (AWTException e) {
            view.showDialog(lang.get("p.macro_dialog.title"),
                    new String[] {lang.get("p.macro_dialog.msg"), lang.get("p.macro_dialog.msg2")},
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void stopCapture() {
        robot.stop();
    }

    @Override
    public void start() {
        prepareView();
        view.showView();
    }

    @Override
    public void addLog(String message) {
        String time = LocalTime.now().format(TIME_FORMATTER);
        view.addLog('[' + time + "] " + message);
    }

    @Override
    public void showLicense() {
        licensePresenter.start();
    }

    @Override
    public void viewOpened() {
        HookWrapper.disableLogging();
        HookWrapper.setSwingEventDispatcher();
        try {
            HookWrapper.register();
            enableKeyListener();

            if (config.isConfigExists()) {
                config.load();
                updateView(settingsModel.getData());
            }
        } catch (NativeHookException e) {
            view.showDialog(lang.get("p.hook_dialog.title"),
                    new String[] {lang.get("p.hook_dialog.msg"), lang.get("p.hook_dialog.msg2")},
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            view.showDialog(lang.get("p.config.read_dialog.title"),
                    new String[] {lang.get("p.config.read_dialog.error_msg"),
                            lang.get("p.config.read_dialog.default")}, JOptionPane.ERROR_MESSAGE);
        } catch (JsonSyntaxException e) {
            view.showDialog(lang.get("p.config.read_dialog.title"),
                    new String[] {lang.get("p.config.read_dialog.syntax_msg"),
                            lang.get("p.config.read_dialog.default")}, JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void viewClosing() {
        robot.forceStop();
        settingsModel.setData(getSettingsDataFromView());
        try {
            config.save();
            HookWrapper.unregister();
        } catch (IOException e) {
            view.showDialog(lang.get("p.config.write_dialog.title"),
                    new String[] {lang.get("p.config.write_dialog.msg")},
                    JOptionPane.ERROR_MESSAGE);
        } catch (NativeHookException ignored) {
        }

        licensePresenter.stop();
    }
}


class MacroNativeKeyListener implements NativeKeyListener {
    private final MacroPresenter presenter;

    public MacroNativeKeyListener(MacroPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_END) {
            presenter.stopCapture();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        switch (nativeEvent.getKeyCode()) {
            case NativeKeyEvent.VC_UP -> {
                if ((nativeEvent.getModifiers() & NativeInputEvent.CTRL_MASK) != 0) {
                    presenter.selectUp();
                }
            }
            case NativeKeyEvent.VC_DOWN -> {
                if ((nativeEvent.getModifiers() & NativeInputEvent.CTRL_MASK) != 0) {
                    presenter.selectDown();
                }
            }
            case NativeKeyEvent.VC_HOME -> {
                if (nativeEvent.getModifiers() == 0) {
                    presenter.startCapture();
                }
            }
            default -> {
            }
        }
    }
}


class MacroRobot {
    private final MacroPresenter presenter;

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
                thread = null; // NOPMD
            }

            if (isDone) {
                presenter.addLog(presenter.lang.get("p.capture_is_done"));
            }
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
        if (thread != null) {
            thread.interrupt();
        }
    }
}


class MacroRobotRunner implements Runnable {
    private final Consumer<Boolean> onReturn;
    private final MacroPresenter presenter;
    private final Robot robot;
    private final SettingsData settingsData;
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

    @Override
    public void run() {
        if (settingsData.count > 0) {
            int movingDelay = settingsData.movingDelay - (settingsData.inputDuration << 2);

            try {
                int i = 1;
                while (true) {
                    pressKeyWithMod(KeyEvent.VK_ALT, KeyEvent.VK_PRINTSCREEN,
                            settingsData.captureDuration);

                    if (i < settingsData.count) {
                        pressKey(directionKeyCode, settingsData.inputDuration);
                    }

                    pressKeyWithMod(KeyEvent.VK_ALT, analyzeKeyCode, settingsData.inputDuration);
                    presenter.addLog(String.format("(%d/%d)", i, settingsData.count));

                    if (i >= settingsData.count) {
                        break;
                    }

                    ++i;
                    if (movingDelay > 0) {
                        Thread.sleep(movingDelay);
                    }
                }
            } catch (InterruptedException e) {
                onReturn.accept(false);
                return;
            }
        }

        onReturn.accept(true);
    }

    private void getKeyCode() {
        analyzeKeyCode = switch (settingsData.analyzeKey) {
            case ALT_F11 -> KeyEvent.VK_F11;
            case ALT_F12 -> KeyEvent.VK_F12;
            case ALT_HOME -> KeyEvent.VK_HOME;
            case ALT_INS -> KeyEvent.VK_INSERT;
        };

        directionKeyCode = switch (settingsData.directionKey) {
            case DOWN -> KeyEvent.VK_DOWN;
            case UP -> KeyEvent.VK_UP;
        };
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
