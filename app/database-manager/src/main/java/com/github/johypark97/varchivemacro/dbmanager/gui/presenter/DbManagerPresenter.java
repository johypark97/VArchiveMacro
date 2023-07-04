package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.Presenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.View;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.CacheCaptureTask;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.DatabaseValidator;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.RemoteValidator;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.TaskRunner;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.DefaultSongViewModel;
import com.github.johypark97.varchivemacro.lib.common.HookWrapper;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.AWTException;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.concurrent.Callable;

public class DbManagerPresenter implements Presenter {
    // task runner
    private final TaskRunner taskRunner;

    // view
    private final Class<? extends View> viewClass;
    private View view;

    // models
    private SongModel songModel;

    public DbManagerPresenter(Class<? extends View> viewClass) {
        this.viewClass = viewClass;

        taskRunner = new TaskRunner((e) -> {
            e.printStackTrace(); // NOPMD
            return null;
        });
    }

    public void setModels(SongModel songModel) {
        this.songModel = songModel;
    }

    private void newView() {
        try {
            view = viewClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        view.setPresenter(this);
    }

    private void runTask(Callable<Void> task) {
        if (!taskRunner.run(task)) {
            view.showErrorDialog("another task is already running");
        }
    }

    private void setupHook() throws NativeHookException {
        HookWrapper.register();
        HookWrapper.addKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_END) {
                    taskRunner.cancel();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                int mod = nativeEvent.getModifiers();

                boolean ctrl = (mod & NativeKeyEvent.CTRL_MASK) != 0;
                mod &= ~NativeKeyEvent.CTRL_MASK;

                boolean alt = (mod & NativeKeyEvent.ALT_MASK) != 0;
                mod &= ~NativeKeyEvent.ALT_MASK;

                boolean shift = (mod & NativeKeyEvent.SHIFT_MASK) != 0;
                mod &= ~NativeKeyEvent.SHIFT_MASK;

                boolean otherMod = mod != 0;
                if (otherMod) {
                    return;
                }

                if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_HOME) {
                    if (ctrl && !alt && !shift) {
                        if (!songModel.isLoaded()) {
                            view.showErrorDialog("The song model is not loaded");
                            return;
                        }

                        CacheCaptureTask cacheCaptureTask;
                        try {
                            cacheCaptureTask = new CacheCaptureTask();
                        } catch (AWTException e) {
                            throw new RuntimeException(e);
                        }

                        cacheCaptureTask.setConfig(view.getCacheGeneratorConfig());
                        cacheCaptureTask.setSongList(songModel.getSongList());

                        runTask(cacheCaptureTask);
                    }
                }
            }
        });
    }

    private void clearHook() {
        try {
            HookWrapper.unregister();
        } catch (NativeHookException ignored) {
        }
    }

    @Override
    public synchronized void start() {
        if (view != null) {
            return;
        }
        newView();

        view.showView();
    }

    @Override
    public synchronized void stop() {
        clearHook();
        view.disposeView();
    }

    @Override
    public void viewOpened() {
        try {
            setupHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadDatabase(String path) {
        try {
            songModel.load(Path.of(path));
            view.setViewModels(new DefaultSongViewModel(songModel));
        } catch (IOException e) {
            view.showErrorDialog("Cannot read the file");
        } catch (RuntimeException e) {
            view.showErrorDialog(e.getMessage());
        }
    }

    @Override
    public void validateDatabase() {
        if (!songModel.isLoaded()) {
            view.showErrorDialog("Database not loaded");
            return;
        }

        DatabaseValidator validator = new DatabaseValidator(songModel);
        view.setValidatorResultText(validator.validate());
    }

    @Override
    public void checkRemote() {
        if (!songModel.isLoaded()) {
            view.showErrorDialog("Database not loaded");
            return;
        }

        try {
            RemoteValidator validator = new RemoteValidator(songModel);
            view.setCheckerResultText(validator.validate());
        } catch (GeneralSecurityException e) {
            view.showErrorDialog("TLS Init Error");
        } catch (IOException e) {
            view.showErrorDialog(e.getMessage());
        } catch (InterruptedException ignored) {
        }
    }
}
