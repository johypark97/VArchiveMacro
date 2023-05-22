package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.api.Api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Pattern;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import java.util.Map;

public interface IRecordManager {
    boolean updateRecord(LocalRecord record);

    LocalRecord getRecord(int id, Button button, Pattern pattern);

    Map<Button, Map<Pattern, String>> getRecordMap(int id);
}
