package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.CollectionTaskData;

public interface ScannerTaskModels {
    interface TaskDataProvider {
        CollectionTaskData getTaskData(int taskNumber) throws Exception;
    }


    interface IScannerTaskModel {
        CollectionTaskData getTaskData(int taskNumber) throws Exception;
    }


    class ScannerTaskModel implements IScannerTaskModel {
        private final TaskDataProvider taskDataProvider;

        public ScannerTaskModel(TaskDataProvider taskDataProvider) {
            this.taskDataProvider = taskDataProvider;
        }

        @Override
        public CollectionTaskData getTaskData(int taskNumber) throws Exception {
            return taskDataProvider.getTaskData(taskNumber);
        }
    }
}
