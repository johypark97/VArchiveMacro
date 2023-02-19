package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.RecordManager;
import com.github.johypark97.varchivemacro.macro.command.Command;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class RecordModel {
    private static final Path BASE_PATH = Path.of(System.getProperty("user.dir"));
    private static final Path RECORD_PATH = BASE_PATH.resolve("records.json");

    private final RecordManager recordManager = new RecordManager();

    private final Consumer<Exception> whenThrown;
    private final Runnable whenDone;

    public RecordModel(Runnable whenDone, Consumer<Exception> whenThrown) {
        this.whenDone = whenDone;
        this.whenThrown = whenThrown;
    }

    public List<Float> getRecords(int id) {
        return recordManager.getRecords(id);
    }

    public boolean loadLocal() throws IOException {
        if (!Files.exists(RECORD_PATH)) {
            return false;
        }

        recordManager.loadJson(RECORD_PATH);
        return true;
    }

    public Command getCommand_loadRemote(String djName) {
        return () -> {
            try {
                recordManager.loadRemote(djName);
                recordManager.saveJson(RECORD_PATH);
            } catch (Exception e) {
                whenThrown.accept(e);
                return;
            }

            whenDone.run();
        };
    }

    public void save() throws IOException {
        recordManager.saveJson(RECORD_PATH);
    }
}
