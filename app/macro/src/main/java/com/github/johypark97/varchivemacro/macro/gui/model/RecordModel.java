package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.RecordManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;

public class RecordModel {
    private static final Path BASE_PATH = Path.of(System.getProperty("user.dir"));

    private static final Path RECORD_PATH = BASE_PATH.resolve("records.json");

    private final RecordManager recordManager = new RecordManager();

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

    public void loadRemote(String djName)
            throws GeneralSecurityException, IOException, InterruptedException {
        recordManager.loadRemote(djName);
        recordManager.saveJson(RECORD_PATH);
    }

    public void save() throws IOException {
        recordManager.saveJson(RECORD_PATH);
    }
}
