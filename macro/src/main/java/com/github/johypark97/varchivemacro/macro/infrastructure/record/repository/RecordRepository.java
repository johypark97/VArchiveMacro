package com.github.johypark97.varchivemacro.macro.infrastructure.record.repository;

import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface RecordRepository {
    void save() throws IOException;

    boolean loadLocal() throws IOException;

    void loadRemote(String djName, Consumer<Boolean> onDone, BiConsumer<String, Exception> onThrow);

    List<LocalRecord> getRecordList(int id);

    LocalRecord findSameRecord(LocalRecord record);

    void updateRecord(LocalRecord record);
}
