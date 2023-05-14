package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.hook.HookWrapper;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
import com.github.johypark97.varchivemacro.macro.core.command.CommandRunner;
import com.github.johypark97.varchivemacro.macro.gui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.gui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModel.TaskModel;
import com.github.johypark97.varchivemacro.macro.gui.model.SongModel;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CollectionTaskData;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CollectionTaskData.RecordData;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.Scanner;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.View;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.ScannerTaskViewData;
import com.github.johypark97.varchivemacro.macro.gui.presenter.MacroCommandBuilder.Direction;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.ScannerTaskViewModel.TaskViewModel;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.johypark97.varchivemacro.macro.resource.MacroPresenterKey;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroPresenter implements Presenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MacroPresenter.class);
    private static final String ERROR_LOG_PREFIX = "Error: ";
    private static final String COLON = ": ";

    // model
    private SongModel songModel;
    private final CommandRunner commandRunner = new CommandRunner();
    private final ConfigModel configModel = new ConfigModel();
    private final MacroCommandBuilder macroCommandBuilder = new MacroCommandBuilder();
    private final RecordModel recordModel = new RecordModel();
    private final Scanner scanner = new Scanner();
    private final TaskModel scannerTaskModel = new TaskModel();

    // view
    private final Class<? extends View> viewClass;
    public View view;

    // other presenters
    private IExpected.Presenter expectedPresenter;
    private ILicense.Presenter licensePresenter;
    private IScannerTask.Presenter scannerTaskPresenter;

    // variables
    private final Language lang = Language.getInstance();

    public MacroPresenter(Class<? extends View> viewClass) {
        Runnable whenDone = () -> {
            Toolkit.getDefaultToolkit().beep();
            view.addLog(lang.get(MacroPresenterKey.WHEN_DONE));
        };
        Runnable whenCanceled = () -> {
            Toolkit.getDefaultToolkit().beep();
            view.addLog(lang.get(MacroPresenterKey.WHEN_CANCELED));
        };
        Consumer<Exception> whenThrown = (e) -> {
            Toolkit.getDefaultToolkit().beep();
            LOGGER.atError().log("", e);
            view.addLog(lang.get(MacroPresenterKey.WHEN_THROWN));
            view.addLog(ERROR_LOG_PREFIX + e.getMessage());
        };

        recordModel.whenDone = whenDone;
        recordModel.whenThrown = whenThrown;

        recordModel.whenStart_loadRemote = (djName) -> view.addLog(
                lang.get(MacroPresenterKey.WHEN_START_LOAD_REMOTE) + " " + djName);

        scanner.whenCanceled = whenCanceled;
        scanner.whenDone = whenDone;
        scanner.whenThrown = whenThrown;

        scanner.whenCaptureDone = () -> {
            Toolkit.getDefaultToolkit().beep();
            view.addLog(lang.get(MacroPresenterKey.WHEN_CAPTURE_DONE));
        };
        scanner.whenStart_analyze =
                () -> view.addLog(lang.get(MacroPresenterKey.WHEN_START_ANALYZE));
        scanner.whenStart_capture =
                () -> view.addLog(lang.get(MacroPresenterKey.WHEN_START_CAPTURE));
        scanner.whenStart_collectResult =
                () -> view.addLog(lang.get(MacroPresenterKey.WHEN_START_COLLECT_RESULT));
        scanner.whenStart_loadImages =
                () -> view.addLog(lang.get(MacroPresenterKey.WHEN_START_LOAD_IMAGES));
        scanner.whenStart_uploadRecord =
                () -> view.addLog(lang.get(MacroPresenterKey.WHEN_START_UPLOAD_RECORD));

        macroCommandBuilder.whenCanceled = whenCanceled;
        macroCommandBuilder.whenDone = whenDone;
        macroCommandBuilder.whenStart =
                () -> view.addLog(lang.get(MacroPresenterKey.WHEN_START_MACRO));
        macroCommandBuilder.whenThrown = whenThrown;

        this.viewClass = viewClass;
    }

    public void setPresenters(IExpected.Presenter expectedPresenter,
            ILicense.Presenter licensePresenter, IScannerTask.Presenter scannerTaskPresenter) {
        this.expectedPresenter = expectedPresenter;
        this.licensePresenter = licensePresenter;
        this.scannerTaskPresenter = scannerTaskPresenter;
    }

    private void newView() {
        try {
            view = viewClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        view.setPresenter(this);
    }

    private TreeModel createTabSongTreeModel(String title,
            Map<String, List<LocalSong>> tabSongMap) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(title);

        tabSongMap.forEach((key, value) -> {
            DefaultMutableTreeNode dlcNode = new DefaultMutableTreeNode(key);

            value.forEach((localSong) -> {
                DefaultMutableTreeNode songNode = new DefaultMutableTreeNode(localSong) {
                    @Serial
                    private static final long serialVersionUID = 2139231854201218074L;

                    @Override
                    public String toString() {
                        LocalSong song = (LocalSong) getUserObject();
                        return String.format("%s ...... %s", song.title(), song.composer());
                    }
                };

                dlcNode.add(songNode);
            });

            root.add(dlcNode);
        });

        return new DefaultTreeModel(root);
    }

    private void startMacro(boolean isDirectionUp) {
        macroCommandBuilder.analyzeKey = view.getMacroAnalyzeKey();
        macroCommandBuilder.captureDelay = view.getMacroCaptureDelay();
        macroCommandBuilder.captureDuration = view.getMacroCaptureDuration();
        macroCommandBuilder.count = view.getMacroCount();
        macroCommandBuilder.direction = isDirectionUp ? Direction.UP : Direction.DOWN;
        macroCommandBuilder.keyInputDuration = view.getMacroKeyInputDuration();

        startCommand(macroCommandBuilder.create());
    }

    private void startCommand(Command command) {
        if (commandRunner.start(command)) {
            view.addLog(lang.get(MacroPresenterKey.START_COMMAND));
        } else {
            view.addLog(lang.get(MacroPresenterKey.COMMAND_IS_RUNNING));
        }
    }

    private void setupHook() throws NativeHookException {
        HookWrapper.disableLogging();
        HookWrapper.register();

        HookWrapper.addKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_END) {
                    stopCommand();
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

                // start scanning
                if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_HOME) {
                    if (ctrl && !alt && !shift) {
                        Path path = view.getCacheDir();
                        int captureDelay = view.getScannerCaptureDelay();
                        int inputDuration = view.getScannerKeyInputDuration();

                        Set<String> ownedDlcTabs = view.getSelectedDlcTabs();
                        Map<String, List<LocalSong>> tapSongMap =
                                songModel.getTabSongMap(ownedDlcTabs);

                        Command command = scanner.getCommand_scan(path, captureDelay, inputDuration,
                                tapSongMap);
                        startCommand(command);
                    }
                }

                // start the client macro
                if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_OPEN_BRACKET
                        || nativeEvent.getKeyCode() == NativeKeyEvent.VC_CLOSE_BRACKET) {
                    if (!ctrl && alt && !shift) {
                        startMacro(nativeEvent.getKeyCode() == NativeKeyEvent.VC_OPEN_BRACKET);
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
        if (commandRunner.isRunning()) {
            view.addLog(lang.get(MacroPresenterKey.COMMAND_IS_RUNNING_CANNOT_EXIT));
            return;
        }

        configModel.setAccountPath(view.getAccountPath());
        configModel.setCacheDir(view.getCacheDir());
        configModel.setMacroAnalyzeKey(view.getMacroAnalyzeKey());
        configModel.setMacroCaptureDelay(view.getMacroCaptureDelay());
        configModel.setMacroCaptureDuration(view.getMacroCaptureDuration());
        configModel.setMacroCount(view.getMacroCount());
        configModel.setMacroKeyInputDuration(view.getMacroKeyInputDuration());
        configModel.setRecordUploadDelay(view.getRecordUploadDelay());
        configModel.setScannerCaptureDelay(view.getScannerCaptureDelay());
        configModel.setScannerKeyInputDuration(view.getScannerKeyInputDuration());
        configModel.setSelectedDlcTabs(view.getSelectedDlcTabs());

        try {
            configModel.save();
        } catch (Exception e) {
            LOGGER.atError().log("", e);
        }

        clearHook();

        view.disposeView();
    }

    @Override
    public void viewOpened() {
        try {
            setupHook();
        } catch (NativeHookException e) {
            view.showErrorDialog("Hook error: " + e.getMessage());
            stop();
            return;
        } catch (Exception e) {
            LOGGER.atError().log("", e);
            view.showErrorDialog(ERROR_LOG_PREFIX + e.getMessage());
            stop();
            return;
        }

        try {
            songModel = new SongModel();
        } catch (IOException e) {
            view.showErrorDialog("File read error: " + e.getMessage());
            stop();
            return;
        } catch (Exception e) {
            LOGGER.atError().log("", e);
            view.showErrorDialog(ERROR_LOG_PREFIX + e.getMessage());
            stop();
            return;
        }

        scanner.setModels(songModel, recordModel, scannerTaskModel);

        TreeModel treeModel = createTabSongTreeModel("Records", songModel.getTabSongMap());
        view.setRecordViewerTreeModel(treeModel);
        view.setSelectableDlcTabs(songModel.getTabs());

        TaskViewModel taskViewModel = new TaskViewModel();
        scannerTaskModel.linkModel(taskViewModel);

        view.setScannerResultTableModel(scanner.getResultTableModel());
        view.setScannerResultTableRowSorter(scanner.getResultTableRowSorter());
        view.setScannerTaskTableModel(taskViewModel);

        try {
            configModel.load();
        } catch (IOException e) {
            view.addLog(lang.get(MacroPresenterKey.LOADING_CONFIG_ERROR) + COLON + e.getMessage());
        } catch (Exception e) {
            LOGGER.atError().log("", e);
            view.addLog(lang.get(MacroPresenterKey.LOADING_CONFIG_EXCEPTION));
            view.addLog(ERROR_LOG_PREFIX + e.getMessage());
        }

        view.setAccountPath(configModel.getAccountPath());
        view.setCacheDir(configModel.getCacheDir());
        view.setMacroAnalyzeKey(configModel.getMacroAnalyzeKey());
        view.setMacroCaptureDelay(configModel.getMacroCaptureDelay());
        view.setMacroCaptureDuration(configModel.getMacroCaptureDuration());
        view.setMacroCount(configModel.getMacroCount());
        view.setMacroKeyInputDuration(configModel.getMacroKeyInputDuration());
        view.setRecordUploadDelay(configModel.getRecordUploadDelay());
        view.setScannerCaptureDelay(configModel.getScannerCaptureDelay());
        view.setScannerKeyInputDuration(configModel.getScannerKeyInputDuration());
        view.setSelectedDlcTabs(configModel.getSelectedDlcTabs());

        try {
            if (recordModel.loadLocal()) {
                view.addLog(lang.get(MacroPresenterKey.LOADING_RECORD_LOADED));
            } else {
                String message = lang.get(MacroPresenterKey.LOADING_RECORD_PLEASE_LOAD);
                view.addLog(message);
                view.showMessageDialog(lang.get(MacroPresenterKey.LOADING_RECORD_FILE_NOT_FOUND),
                        message);
            }
        } catch (IOException e) {
            view.addLog(lang.get(MacroPresenterKey.LOADING_RECORD_ERROR) + COLON + e.getMessage());
        } catch (Exception e) {
            LOGGER.atError().log("", e);
            view.addLog(lang.get(MacroPresenterKey.LOADING_RECORD_EXCEPTION));
            view.addLog(ERROR_LOG_PREFIX + e.getMessage());
        }
    }

    @Override
    public void viewClosed() {
        if (view != null) {
            view = null; // NOPMD
        }
    }

    @Override
    public void openLicenseView(JFrame frame) {
        licensePresenter.start(frame);
    }

    @Override
    public void changeLanguage(Locale locale) {
        Language.saveLocale(locale);
        view.showMessageDialog("", lang.get(MacroPresenterKey.CHANGE_LANGUAGE));
    }

    @Override
    public void loadServerRecord(String djName) {
        Command command = recordModel.getCommand_loadRemote(djName);
        startCommand(command);
    }

    @Override
    public void recordViewerTreeNodeSelected(Object object) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
        if (!node.isLeaf()) {
            return;
        }

        LocalSong song = (LocalSong) node.getUserObject();

        String newline = System.lineSeparator();
        StringBuilder builder = new StringBuilder();
        builder.append("Title: ").append(song.title()).append(newline);
        builder.append("Composer: ").append(song.composer()).append(newline);
        builder.append("DLC: ").append(song.dlc()).append(newline);
        builder.append("DLC Tab: ").append(song.dlcTab());

        view.showRecord(builder.toString(), recordModel.getRecords(song.id()));
    }

    @Override
    public void openExpected(JFrame frame) {
        Set<String> selectedTabs = view.getSelectedDlcTabs();
        Map<String, List<LocalSong>> tabSongMap = songModel.getTabSongMap(selectedTabs);
        expectedPresenter.start(frame, createTabSongTreeModel("List", tabSongMap));
    }

    @Override
    public void showScannerTask(JFrame frame, int taskNumber) {
        CollectionTaskData taskData;
        try {
            taskData = scanner.getTaskData(taskNumber);
        } catch (Exception e) {
            view.addLog(lang.get(MacroPresenterKey.LOADING_TASK_DATA_EXCEPTION));
            view.addLog(ERROR_LOG_PREFIX + e.getMessage());
            return;
        }

        if (taskData == null) {
            return;
        }

        if (taskData.exception != null) {
            view.addLog(lang.get(MacroPresenterKey.LOADING_TASK_DATA_OCCURRED) + COLON
                    + taskData.exception.getMessage());
            return;
        }

        ScannerTaskViewData viewData = new ScannerTaskViewData();
        viewData.fullImage = taskData.fullImage;
        viewData.titleImage = taskData.titleImage;
        taskData.records.cellSet().forEach((cell) -> {
            Button button = cell.getRowKey();
            Pattern pattern = cell.getColumnKey();
            RecordData data = cell.getValue();
            viewData.addRecord(button, pattern, data.rateImage, data.maxComboImage, data.rate,
                    data.maxCombo);
        });

        scannerTaskPresenter.start(frame, viewData);
    }

    @Override
    public void loadCachedImages() {
        Path path = view.getCacheDir();
        Map<String, List<LocalSong>> tapSongMap = songModel.getTabSongMap();
        Command command = scanner.getCommand_loadCachedImages(path, tapSongMap);
        startCommand(command);
    }

    @Override
    public void analyzeScannerTask() {
        Command command = scanner.getCommand_analyze();
        startCommand(command);
    }

    @Override
    public void refreshScannerResult() {
        Command command = scanner.getCommand_collectResult();
        startCommand(command);
    }

    @Override
    public void uploadRecord(Path accountPath) {
        int uploadDelay = view.getRecordUploadDelay();
        Command command = scanner.getCommand_uploadRecord(accountPath, uploadDelay);
        startCommand(command);
    }

    @Override
    public void stopCommand() {
        if (!commandRunner.stop()) {
            view.addLog(lang.get(MacroPresenterKey.COMMAND_IS_NOT_RUNNING));
        }
    }
}
