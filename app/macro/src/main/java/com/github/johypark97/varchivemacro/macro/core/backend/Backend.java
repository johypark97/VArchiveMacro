package com.github.johypark97.varchivemacro.macro.core.backend;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.ISongRecordManager;
import com.github.johypark97.varchivemacro.macro.core.SongRecordManager;
import com.github.johypark97.varchivemacro.macro.core.backend.BackendEvent.Type;
import com.github.johypark97.varchivemacro.macro.core.clientmacro.AnalyzeKey;
import com.github.johypark97.varchivemacro.macro.core.clientmacro.ClientMacro;
import com.github.johypark97.varchivemacro.macro.core.clientmacro.Direction;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
import com.github.johypark97.varchivemacro.macro.core.command.CommandRunner;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Server;
import com.github.johypark97.varchivemacro.macro.core.scanner.Scanner;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResultListProvider;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.Event;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.TaskListProvider;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.TaskDataProvider;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Backend implements Server<BackendEvent, IBackend>, IBackend {
    private final CommandRunner commandRunner = new CommandRunner();
    private final List<Client<BackendEvent, IBackend>> clientList = new CopyOnWriteArrayList<>();

    private final ClientMacro clientMacro;
    private final Scanner scanner;
    private final SongRecordManager songRecordManager;

    public Backend() {
        Consumer<Exception> whenThrown = (x) -> notifyClients(new BackendEvent(x));
        Runnable whenCanceled = createWhen(Type.CANCELED);
        Runnable whenDone = createWhen(Type.DONE);

        Consumer<String> whenStart_loadRemoteRecord = (djName) -> notifyClients(
                new BackendEvent(Type.LOAD_REMOTE_RECORD, List.of(djName)));
        songRecordManager = new SongRecordManager(whenThrown, whenStart_loadRemoteRecord, whenDone);

        Runnable whenStart_analyze = createWhen(Type.SCANNER_START_ANALYZE);
        Runnable whenStart_capture = createWhen(Type.SCANNER_START_CAPTURE);
        Runnable whenStart_collectResult = createWhen(Type.SCANNER_START_COLLECT_RESULT);
        Runnable whenStart_uploadRecord = createWhen(Type.SCANNER_START_UPLOAD_RECORD);
        scanner = new Scanner(whenThrown, whenCanceled, whenDone, whenStart_analyze,
                whenStart_capture, whenStart_collectResult, whenStart_uploadRecord);

        Runnable whenStart_macro = createWhen(Type.CLIENT_MACRO_START);
        clientMacro = new ClientMacro(whenThrown, whenCanceled, whenDone, whenStart_macro);

        scanner.setModels(songRecordManager);
    }

    public ISongRecordManager getSongRecordManager() {
        return songRecordManager;
    }

    public TaskDataProvider getTaskDataProvider() {
        return scanner.getTaskDataProvider();
    }

    public void addTaskListClient(Client<Event, TaskListProvider> client) {
        scanner.addTaskListClient(client);
    }

    public void addResultListClient(
            Client<ScannerResultListModels.Event, ResultListProvider> client) {
        scanner.addResultListClient(client);
    }

    private Runnable createWhen(Type type) {
        return () -> notifyClients(new BackendEvent(type));
    }

    private synchronized void startCommand(Command command) {
        if (commandRunner.isRunning()) {
            notifyClients(new BackendEvent(Type.IS_RUNNING));
            return;
        }

        notifyClients(new BackendEvent(Type.START_COMMAND));
        commandRunner.start(command);
    }

    @Override
    public void addClient(Client<BackendEvent, IBackend> client) {
        clientList.add(client);
        client.onAddClient(this);
    }

    @Override
    public void notifyClients(BackendEvent data) {
        clientList.forEach((x) -> x.onNotify(data));
    }

    @Override
    public boolean loadSongs() throws IOException {
        return songRecordManager.loadSongs();
    }

    @Override
    public boolean loadLocalRecord() throws IOException {
        return songRecordManager.loadLocalRecord();
    }

    @Override
    public boolean isCommandRunning() {
        return commandRunner.isRunning();
    }

    @Override
    public void stopCommand() {
        if (!commandRunner.stop()) {
            notifyClients(new BackendEvent(Type.IS_NOT_RUNNING));
        }
    }

    @Override
    public void loadRemoteRecord(String djName) {
        Command command = songRecordManager.createCommand_loadRemoteRecord(djName);
        startCommand(command);
    }

    @Override
    public void runClientMacro(AnalyzeKey analyzeKey, Direction direction, int captureDelay,
            int captureDuration, int count, int keyInputDuration) {
        Command command =
                clientMacro.createCommand(analyzeKey, direction, captureDelay, captureDuration,
                        count, keyInputDuration);
        startCommand(command);
    }

    @Override
    public void startScan(Path cacheDir, int captureDelay, int inputDuration,
            Set<String> ownedDlcTabs) {
        Map<String, List<LocalSong>> tabSongMap = songRecordManager.getTabSongMap(ownedDlcTabs);
        Command command =
                scanner.getCommand_safeScan(cacheDir, captureDelay, inputDuration, tabSongMap);
        startCommand(command);
    }

    @Override
    public void startAnalyze() {
        Command command = scanner.getCommand_analyze();
        startCommand(command);
    }

    @Override
    public void collectResult() {
        Command command = scanner.getCommand_collectResult();
        startCommand(command);
    }

    @Override
    public void uploadRecord(Path accountPath, int uploadDelay) {
        Command command = scanner.getCommand_uploadRecord(accountPath, uploadDelay);
        startCommand(command);
    }
}