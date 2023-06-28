package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.Enums.Button;
import com.github.johypark97.varchivemacro.lib.common.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import java.util.Map;

public interface RecordManager {
    boolean updateRecord(LocalRecord record);

    LocalRecord findSameRecord(LocalRecord record);

    LocalRecord getRecord(int id, Button button, Pattern pattern);

    Map<Pattern, LocalRecord> getRecord(int id, Button button);

    Map<Button, Map<Pattern, LocalRecord>> getRecord(int id);
}
