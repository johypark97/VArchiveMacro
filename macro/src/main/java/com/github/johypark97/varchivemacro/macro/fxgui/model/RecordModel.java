package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface RecordModel {
    boolean loadLocal() throws IOException;

    void loadRemote(String djName, Consumer<Boolean> onDone, BiConsumer<String, Exception> onThrow);

    List<LocalRecord> getRecordList(int id);
}
