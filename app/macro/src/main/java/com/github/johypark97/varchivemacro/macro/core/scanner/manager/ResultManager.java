package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.exception.RecordNotLoadedException;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.ResultManager.ResultData;

public interface ResultManager extends Iterable<ResultData> {
    void clear();

    void addAll(TaskManager taskManager) throws RecordNotLoadedException;

    enum ResultStatus {
        CANCELED, HIGHER_RECORD_EXISTS, NOT_UPLOADED, SUSPENDED, UPLOADED, UPLOADING
    }


    interface ResultData {
        LocalRecord getNewRecord();

        LocalSong getSong();

        boolean getOldMaxCombo();

        float getOldRate();

        int getResultNumber();

        int getTaskNumber();

        ResultStatus getStatus();

        void setStatus(ResultStatus value);

        boolean isSelected();

        void updateSelected(boolean value);
    }
}
