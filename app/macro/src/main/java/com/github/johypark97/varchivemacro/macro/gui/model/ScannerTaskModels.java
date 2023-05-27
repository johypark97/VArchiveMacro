package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.awt.Image;

public interface ScannerTaskModels {
    interface TaskDataProvider {
        ResponseData getValue(int taskNumber) throws Exception;
    }


    interface IScannerTaskModel {
        ResponseData getData(int taskNumber) throws Exception;
    }


    class ResponseData {
        public final Table<Button, Pattern, RecordData> recordTable = HashBasedTable.create();

        public Exception exception;
        public Image fullImage;
        public Image titleImage;

        public void addRecord(Button button, Pattern pattern, RecordData data) {
            recordTable.put(button, pattern, data);
        }

        public static class RecordData {
            public Image maxComboImage;
            public Image rateImage;
            public String rate;
            public boolean maxCombo;
        }
    }


    class ScannerTaskModel implements IScannerTaskModel {
        private final TaskDataProvider taskDataProvider;

        public ScannerTaskModel(TaskDataProvider taskDataProvider) {
            this.taskDataProvider = taskDataProvider;
        }

        @Override
        public ResponseData getData(int taskNumber) throws Exception {
            return taskDataProvider.getValue(taskNumber);
        }
    }
}
