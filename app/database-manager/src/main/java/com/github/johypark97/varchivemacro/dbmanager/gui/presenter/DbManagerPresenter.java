package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.OcrTesterModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.Presenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.View;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.CacheCaptureTask;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.DatabaseValidator;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.GroundTruthGenerateTask;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.GroundTruthPrepareTask;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.OcrTestTask;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.RemoteValidator;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.TaskRunner;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.DefaultOcrTesterViewModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.DefaultSongViewModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.OcrTesterViewModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.SongViewModel;
import com.github.johypark97.varchivemacro.lib.common.HookWrapper;
import com.github.johypark97.varchivemacro.lib.common.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrInitializationError;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.AWTException;
import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.JFrame;

public class DbManagerPresenter implements Presenter {
    private static final String SONG_MODEL_NOT_LOADED_MESSAGE = "The song model is not loaded";

    // task runner
    private final TaskRunner taskRunner;

    // view
    private final Class<? extends View> viewClass;
    private View view;

    // view models
    private OcrTesterViewModel ocrTesterViewModel;

    // presenters
    public LiveTesterPresenter liveTesterPresenter;

    // models
    public SongModel songModel;
    public OcrTesterModel ocrTesterModel;

    public DbManagerPresenter(Class<? extends View> viewClass) {
        this.viewClass = viewClass;

        taskRunner = new TaskRunner((e) -> {
            view.showMessageDialog(e.getMessage());
            e.printStackTrace(); // NOPMD
            return null;
        }, () -> view.showMessageDialog("done"));
    }

    public void setModels(SongModel songModel, OcrTesterModel ocrTesterModel) {
        this.ocrTesterModel = ocrTesterModel;
        this.songModel = songModel;
    }

    public void setPresenters(LiveTesterPresenter liveTesterPresenter) {
        this.liveTesterPresenter = liveTesterPresenter;
    }

    private void newView() {
        try {
            view = viewClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        view.setPresenter(this);
    }

    private Callable<Void> createCacheCaptureTask() {
        CacheCaptureTask task;

        try {
            task = new CacheCaptureTask(() -> Toolkit.getDefaultToolkit().beep());
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        task.setConfig(view.getCacheGeneratorConfig());
        task.setSongList(songModel.getSongList());

        return task;
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
                            view.showErrorDialog(SONG_MODEL_NOT_LOADED_MESSAGE);
                            return;
                        }

                        Callable<Void> task = createCacheCaptureTask();
                        runTask(task);
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

            SongViewModel songViewModel = new DefaultSongViewModel(songModel);
            ocrTesterViewModel = new DefaultOcrTesterViewModel(ocrTesterModel);

            view.setViewModels(songViewModel, ocrTesterViewModel);
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

    @Override
    public void prepareGroundTruth() {
        if (!songModel.isLoaded()) {
            view.showErrorDialog(SONG_MODEL_NOT_LOADED_MESSAGE);
            return;
        }

        GroundTruthPrepareTask task = new GroundTruthPrepareTask();
        task.setConfig(view.getGroundTruthGeneratorConfig());
        task.setSongModel(songModel);

        runTask(task);
    }

    @Override
    public void generateGroundTruth() {
        if (!songModel.isLoaded()) {
            view.showErrorDialog(SONG_MODEL_NOT_LOADED_MESSAGE);
            return;
        }

        GroundTruthGenerateTask task = new GroundTruthGenerateTask();
        task.setConfig(view.getGroundTruthGeneratorConfig());
        task.setSongModel(songModel);

        runTask(task);
    }

    @Override
    public void runOcrTest() {
        if (!songModel.isLoaded()) {
            view.showErrorDialog(SONG_MODEL_NOT_LOADED_MESSAGE);
            return;
        }

        OcrTestTask task = new OcrTestTask();
        task.setConfig(view.getOcrTesterConfig());
        task.setModels(songModel, ocrTesterModel);

        Runnable whenChanged = () -> ocrTesterViewModel.notifyDataUpdated();
        Consumer<Integer> whenAdded = (x) -> ocrTesterViewModel.notifyDataAdded(x);
        task.setEvents(whenChanged, whenAdded, whenChanged);

        runTask(task);
    }

    @Override
    public void openLiveTester(JFrame parent) {
        if (!songModel.isLoaded()) {
            view.showErrorDialog(SONG_MODEL_NOT_LOADED_MESSAGE);
            return;
        }

        try {
            liveTesterPresenter.start(parent, songModel, view.getLiveTesterConfig());
        } catch (OcrInitializationError | NotSupportedResolutionException | AWTException |
                IOException e) {
            view.showErrorDialog(e.getMessage());
        }
    }

    @Override
    public void closeLiveTester() {
        liveTesterPresenter.stop();
    }
}
