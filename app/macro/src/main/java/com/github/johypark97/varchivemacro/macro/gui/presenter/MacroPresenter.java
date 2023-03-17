package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.hook.HookWrapper;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
import com.github.johypark97.varchivemacro.macro.core.command.CommandRunner;
import com.github.johypark97.varchivemacro.macro.gui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.gui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.gui.model.SongModel;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CollectionTaskData;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CollectionTaskData.RecordData;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.Scanner;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.View;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.ScannerTaskViewData;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class MacroPresenter implements Presenter {
    private static final String ERROR_LOG_PREFIX = "Error: ";

    // model
    private SongModel songModel;
    private final CommandRunner commandRunner = new CommandRunner();
    private final ConfigModel configModel = new ConfigModel();
    private final RecordModel recordModel = new RecordModel();
    private final Scanner scanner = new Scanner();

    // view
    private final Class<? extends View> viewClass;
    public View view;

    // other presenters
    private IExpected.Presenter expectedPresenter;
    private ILicense.Presenter licensePresenter;
    private IScannerTask.Presenter scannerTaskPresenter;

    public MacroPresenter(Class<? extends View> viewClass) {
        Runnable whenDone = () -> {
            Toolkit.getDefaultToolkit().beep();
            view.addLog("Done.");
        };
        Runnable whenCanceled = () -> {
            Toolkit.getDefaultToolkit().beep();
            view.addLog("Canceled.");
        };
        Consumer<Exception> whenThrown = (e) -> {
            Toolkit.getDefaultToolkit().beep();
            view.addLog(ERROR_LOG_PREFIX + e.getMessage());
        };

        recordModel.whenDone = whenDone;
        recordModel.whenThrown = whenThrown;

        recordModel.whenStart_loadRemote =
                (djName) -> view.addLog("Loading record... DJ Name: " + djName);

        scanner.whenCanceled = whenCanceled;
        scanner.whenDone = whenDone;
        scanner.whenThrown = whenThrown;

        scanner.whenCaptureDone = () -> {
            Toolkit.getDefaultToolkit().beep();
            view.addLog("Capture done.");
        };
        scanner.whenStart_analyze = () -> view.addLog("Analyzing...");
        scanner.whenStart_capture = () -> view.addLog("Scanning...");
        scanner.whenStart_collectResult = () -> view.addLog("Refreshing the result...");
        scanner.whenStart_loadImages = () -> view.addLog("Loading images from disk...");
        scanner.whenStart_uploadRecord = () -> view.addLog("Uploading records...");

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

    private void startCommand(Command command) {
        if (commandRunner.start(command)) {
            view.addLog("-------- Start command --------");
        } else {
            view.addLog("Another command is running.");
        }
    }

    private void setupHook() throws NativeHookException {
        HookWrapper.disableLogging();
        HookWrapper.register();

        HookWrapper.addKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                if ((nativeEvent.getModifiers() & NativeKeyEvent.CTRL_MASK) == 0) {
                    return;
                }

                if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_END) {
                    stopCommand();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                int mod = nativeEvent.getModifiers();

                boolean ctrl = (mod & NativeKeyEvent.CTRL_MASK) != 0;
                mod &= ~NativeKeyEvent.CTRL_MASK;

                boolean shift = (mod & NativeKeyEvent.SHIFT_MASK) != 0;
                mod &= ~NativeKeyEvent.SHIFT_MASK;

                boolean otherMod = mod != 0;
                if (otherMod) {
                    return;
                }

                if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_HOME) {
                    if (ctrl && !shift) {
                        Set<String> ownedDlcTabs = view.getSelectedDlcTabs();
                        Map<String, List<LocalSong>> tapSongMap =
                                songModel.getTabSongMap(ownedDlcTabs);

                        Command command = scanner.getCommand_scan(tapSongMap);
                        startCommand(command);
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
            view.addLog("A command is running. Cannot exit.");
            return;
        }

        configModel.setAccountPath(view.getAccountPath());
        configModel.setSelectedDlcTabs(view.getSelectedDlcTabs());

        try {
            configModel.save();
        } catch (Exception ignored) {
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
            view.showErrorDialog("ERROR: " + e.getMessage());
            stop();
            return;
        }

        scanner.setModels(songModel, recordModel);

        TreeModel treeModel = createTabSongTreeModel("Records", songModel.getTabSongMap());
        view.setRecordViewerTreeModel(treeModel);
        view.setSelectableDlcTabs(songModel.getTabs());

        view.setScannerResultTableModel(scanner.getResultTableModel());
        view.setScannerResultTableRowSorter(scanner.getResultTableRowSorter());
        view.setScannerTaskTableModel(scanner.getTaskTableModel());

        try {
            configModel.load();
        } catch (IOException e) {
            view.addLog("Config file read error: " + e.getMessage());
        } catch (Exception e) {
            view.addLog(ERROR_LOG_PREFIX + e.getMessage());
        }

        view.setAccountPath(configModel.getAccountPath());
        view.setSelectedDlcTabs(configModel.getSelectedDlcTabs());

        try {
            if (recordModel.loadLocal()) {
                view.addLog("Record file loaded.");
            } else {
                String message = "Record file not found. Please load your records form the server.";
                view.addLog(message);
                view.showMessageDialog("Record file not found", message);
            }
        } catch (IOException e) {
            view.addLog("Record file read error: " + e.getMessage());
        } catch (Exception e) {
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

        String text = String.format("Title: %s%nComposer: %s", song.title(), song.composer());
        view.showRecord(text, recordModel.getRecords(song.id()));
    }

    @Override
    public void openExpected(JFrame frame) {
        Set<String> ownedDlcTabs = view.getSelectedDlcTabs();
        Map<String, List<LocalSong>> tabSongMap = songModel.getTabSongMap(ownedDlcTabs);
        expectedPresenter.start(frame, createTabSongTreeModel("Expected Song List", tabSongMap));
    }

    @Override
    public void showScannerTask(JFrame frame, int taskNumber) {
        CollectionTaskData taskData;
        try {
            taskData = scanner.getTaskData(taskNumber);
        } catch (Exception e) {
            view.addLog(ERROR_LOG_PREFIX + e.getMessage());
            return;
        }

        if (taskData == null) {
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
        Map<String, List<LocalSong>> tapSongMap = songModel.getTabSongMap();
        Command command = scanner.getCommand_loadCachedImages(tapSongMap);
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
        if (accountPath == null) {
            view.showErrorDialog("No account file selected");
            return;
        }

        Command command = scanner.getCommand_uploadRecord(accountPath);
        startCommand(command);
    }

    @Override
    public void stopCommand() {
        if (!commandRunner.stop()) {
            view.addLog("No command is running.");
        }
    }
}
