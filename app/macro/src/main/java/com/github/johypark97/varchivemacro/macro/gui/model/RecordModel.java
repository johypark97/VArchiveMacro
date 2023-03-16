package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.RecordManager;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.command.AbstractCommand;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class RecordModel {
    private static final Path RECORD_PATH = Path.of("records.json");

    private final RecordManager recordManager = new RecordManager();

    public Consumer<Exception> whenThrown;
    public Consumer<String> whenStart_loadRemote;
    public Runnable whenDone;

    public LocalRecord findSameRecord(LocalRecord record) {
        return recordManager.getRecord(record.id, record.button, record.pattern);
    }

    public Table<Button, Pattern, String> getRecords(int id) {
        Table<Button, Pattern, String> table = HashBasedTable.create();

        recordManager.getRecords(id).forEach((button, patternMap) -> patternMap.forEach(
                (pattern, value) -> table.put(Button.valueOf(button), Pattern.valueOf(pattern),
                        value)));

        return table;
    }

    public void update(LocalRecord record) {
        recordManager.update(record);
    }

    public boolean loadLocal() throws IOException {
        if (!Files.exists(RECORD_PATH)) {
            return false;
        }

        recordManager.loadJson(RECORD_PATH);
        return true;
    }

    public void save() throws IOException {
        recordManager.saveJson(RECORD_PATH);
    }

    public Command getCommand_loadRemote(String djName) {
        return createCommand_loadRemote(djName);
    }

    protected Command createCommand_loadRemote(String djName) {
        return new AbstractCommand() {
            @Override
            public boolean run() {
                whenStart_loadRemote.accept(djName);

                try {
                    recordManager.loadRemote(djName);
                    recordManager.saveJson(RECORD_PATH);
                } catch (Exception e) {
                    whenThrown.accept(e);
                    return false;
                }

                whenDone.run();
                return true;
            }
        };
    }
}
