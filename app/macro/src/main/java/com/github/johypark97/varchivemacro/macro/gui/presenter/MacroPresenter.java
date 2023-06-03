package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.hook.HookWrapper;
import com.github.johypark97.varchivemacro.macro.core.backend.BackendEvent;
import com.github.johypark97.varchivemacro.macro.core.backend.IBackend;
import com.github.johypark97.varchivemacro.macro.core.clientmacro.AnalyzeKey;
import com.github.johypark97.varchivemacro.macro.core.clientmacro.Direction;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.gui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ScannerResultListModel;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ScannerTaskListModel;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.IScannerTaskModel;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.SongRecordModels.ISongRecordModel;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.View;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.ScannerResultListViewModels.ScannerResultListViewModel;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.ScannerTaskListViewModels.ScannerTaskListViewModel;
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
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroPresenter implements Presenter, Client<BackendEvent, IBackend> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MacroPresenter.class);
    private static final String COLON = ": ";
    private static final String ERROR_LOG_PREFIX = "Error: ";

    // language
    private final Language lang = Language.getInstance();

    // model
    private final ConfigModel configModel = new ConfigModel();

    // view
    private final Class<? extends View> viewClass;
    private View view;

    // backend channel
    private IBackend backend;

    // data models
    private IScannerTaskModel scannerTaskModel;
    private ISongRecordModel songRecordModel;
    private ScannerResultListModel scannerResultListModel;
    private ScannerTaskListModel scannerTaskListModel;

    // other presenters
    private IExpected.Presenter expectedPresenter;
    private ILicense.Presenter licensePresenter;
    private IScannerTask.Presenter scannerTaskPresenter;

    public MacroPresenter(Class<? extends View> viewClass) {
        this.viewClass = viewClass;
    }

    public void setModels(ISongRecordModel songRecordModel, IScannerTaskModel scannerTaskModel,
            ScannerTaskListModel scannerTaskListModel,
            ScannerResultListModel scannerResultListModel) {
        this.scannerResultListModel = scannerResultListModel;
        this.scannerTaskListModel = scannerTaskListModel;
        this.scannerTaskModel = scannerTaskModel;
        this.songRecordModel = songRecordModel;
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

    private void startClientMacro(boolean isDirectionUp) {
        AnalyzeKey analyzeKey = view.getMacroAnalyzeKey();
        Direction direction = isDirectionUp ? Direction.UP : Direction.DOWN;
        int captureDelay = view.getMacroCaptureDelay();
        int captureDuration = view.getMacroCaptureDuration();
        int count = view.getMacroCount();
        int keyInputDuration = view.getMacroKeyInputDuration();

        backend.runClientMacro(analyzeKey, direction, captureDelay, captureDuration, count,
                keyInputDuration);
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
                        Set<String> ownedDlcTabs = view.getSelectedDlcTabs();
                        boolean safeMode = view.getScannerSafeMode();
                        int captureDelay = view.getScannerCaptureDelay();
                        int inputDuration = view.getScannerKeyInputDuration();

                        backend.startScan(path, captureDelay, inputDuration, ownedDlcTabs,
                                safeMode);
                    }
                }

                // start the client macro
                if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_OPEN_BRACKET
                        || nativeEvent.getKeyCode() == NativeKeyEvent.VC_CLOSE_BRACKET) {
                    if (!ctrl && alt && !shift) {
                        startClientMacro(
                                nativeEvent.getKeyCode() == NativeKeyEvent.VC_OPEN_BRACKET);
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
    public void onAddClient(IBackend channel) {
        backend = channel;
    }

    @Override
    public void onNotify(BackendEvent data) {
        switch (data.type) {
            case CANCELED -> {
                Toolkit.getDefaultToolkit().beep();
                view.addLog(lang.get(MacroPresenterKey.WHEN_CANCELED));
            }
            case CLIENT_MACRO_START -> view.addLog(lang.get(MacroPresenterKey.WHEN_START_MACRO));
            case DONE -> {
                Toolkit.getDefaultToolkit().beep();
                view.addLog(lang.get(MacroPresenterKey.WHEN_DONE));
            }
            case EXCEPTION -> {
                Toolkit.getDefaultToolkit().beep();
                LOGGER.atError().log("", data.exception);
                view.addLog(lang.get(MacroPresenterKey.WHEN_THROWN));
                view.addLog(ERROR_LOG_PREFIX + data.exception.getMessage());
            }
            case IS_NOT_RUNNING -> view.addLog(lang.get(MacroPresenterKey.COMMAND_IS_NOT_RUNNING));
            case IS_RUNNING -> view.addLog(lang.get(MacroPresenterKey.COMMAND_IS_RUNNING));
            case LOAD_REMOTE_RECORD -> view.addLog(
                    lang.get(MacroPresenterKey.WHEN_START_LOAD_REMOTE) + " " + (data.argList != null
                            ? data.argList.get(0)
                            : null));
            case SCANNER_START_ANALYZE ->
                    view.addLog(lang.get(MacroPresenterKey.WHEN_START_ANALYZE));
            case SCANNER_START_CAPTURE ->
                    view.addLog(lang.get(MacroPresenterKey.WHEN_START_CAPTURE));
            case SCANNER_START_COLLECT_RESULT ->
                    view.addLog(lang.get(MacroPresenterKey.WHEN_START_COLLECT_RESULT));
            case SCANNER_START_UPLOAD_RECORD ->
                    view.addLog(lang.get(MacroPresenterKey.WHEN_START_UPLOAD_RECORD));
            case START_COMMAND -> view.addLog(lang.get(MacroPresenterKey.START_COMMAND));
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
        if (backend.isCommandRunning()) {
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
            if (!backend.loadSongs()) {
                view.showErrorDialog("All the database files were not found");
                stop();
                return;
            }
        } catch (IOException e) {
            view.showErrorDialog("Database file read error: " + e.getMessage());
            stop();
            return;
        } catch (Exception e) {
            LOGGER.atError().log("", e);
            view.showErrorDialog(ERROR_LOG_PREFIX + e.getMessage());
            stop();
            return;
        }

        TreeModel treeModel = createTabSongTreeModel("Records", songRecordModel.getTabSongMap());
        view.setRecordViewerTreeModel(treeModel);
        view.setSelectableDlcTabs(songRecordModel.getDlcTabList());

        ScannerTaskListViewModel scannerTaskListViewModel = new ScannerTaskListViewModel();
        scannerTaskListModel.linkModel(scannerTaskListViewModel);

        ScannerResultListViewModel scannerResultListViewModel = new ScannerResultListViewModel();
        scannerResultListModel.linkModel(scannerResultListViewModel);

        view.setScannerResultTableModel(scannerResultListViewModel);
        view.setScannerResultTableRowSorter(scannerResultListViewModel.createRowSorter());
        view.setScannerTaskTableModel(scannerTaskListViewModel);
        view.setScannerTaskTableRowSorter(scannerTaskListViewModel.createRowSorter());

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
            if (backend.loadLocalRecord()) {
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
        backend.loadRemoteRecord(djName);
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

        view.showRecord(builder.toString(), songRecordModel.getRecordTable(song.id()));
    }

    @Override
    public void openExpected(JFrame frame) {
        Set<String> selectedTabs = view.getSelectedDlcTabs();
        Map<String, List<LocalSong>> tabSongMap = songRecordModel.getTabSongMap(selectedTabs);
        expectedPresenter.start(frame, createTabSongTreeModel("List", tabSongMap));
    }

    @Override
    public void showScannerTask(JFrame frame, int taskNumber) {
        ResponseData data;
        try {
            data = scannerTaskModel.getData(taskNumber);
        } catch (IOException e) {
            view.addLog(lang.get(MacroPresenterKey.LOADING_TASK_DATA_EXCEPTION));
            view.addLog(ERROR_LOG_PREFIX + e.getMessage());
            return;
        }

        if (data == null) {
            return;
        }

        if (data.exception != null) {
            view.addLog(lang.get(MacroPresenterKey.LOADING_TASK_DATA_OCCURRED) + COLON
                    + data.exception.getMessage());
            return;
        }

        scannerTaskPresenter.start(frame, data);
    }

    @Override
    public void analyzeScannerTask() {
        backend.startAnalyze();
    }

    @Override
    public void refreshScannerResult() {
        backend.collectResult();
    }

    @Override
    public void uploadRecord(Path accountPath) {
        int uploadDelay = view.getRecordUploadDelay();
        backend.uploadRecord(accountPath, uploadDelay);
    }

    @Override
    public void stopCommand() {
        backend.stopCommand();
    }
}
