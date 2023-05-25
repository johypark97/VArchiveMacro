package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.CollectionTaskData;

public interface ScannerTaskDataModel {
    interface TaskDataProvider {
        CollectionTaskData getTaskData(int taskNumber) throws Exception;
    }


    interface IScannerTaskDataModel {
        CollectionTaskData getTaskData(int taskNumber) throws Exception;
    }


    class TaskDataModel implements IScannerTaskDataModel {
        private final TaskDataProvider taskDataProvider;

        public TaskDataModel(TaskDataProvider taskDataProvider) {
            this.taskDataProvider = taskDataProvider;
        }

        @Override
        public CollectionTaskData getTaskData(int taskNumber) throws Exception {
            return taskDataProvider.getTaskData(taskNumber);
        }
    }
}
