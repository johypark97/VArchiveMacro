package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskStatus;

public interface ScannerTaskListModels {
    interface TaskListProvider {
        ResponseData getValue(int index);

        int getCount();
    }


    interface Model {
        void linkModel(ViewModel viewModel);

        int getCount();

        ResponseData getData(int index);
    }


    interface ViewModel {
        void onLinkModel(Model model);

        void onDataChanged();

        void onRowsInserted(int row);

        void onRowsUpdated(int row);
    }


    class Event {
        public final Type type;
        public final int value;

        public Event(Type type, int value) {
            this.type = type;
            this.value = value;
        }

        public Event(Type type) {
            this(type, 0);
        }

        public enum Type {
            DATA_CHANGED, ROWS_INSERTED, ROWS_UPDATED
        }
    }


    class ResponseData {
        public String composer;
        public String dlc;
        public String scannedTitle;
        public String tab;
        public String title;
        public TaskStatus status;
        public boolean valid;
        public int count;
        public int index;
        public int taskNumber;
    }


    class ScannerTaskListModel implements Client<Event, TaskListProvider>, Model {
        private TaskListProvider taskListProvider;
        private ViewModel viewModel;

        @Override
        public void onAddClient(TaskListProvider channel) {
            taskListProvider = channel;
        }

        @Override
        public void onNotify(Event data) {
            switch (data.type) {
                case DATA_CHANGED -> viewModel.onDataChanged();
                case ROWS_INSERTED -> viewModel.onRowsInserted(data.value);
                case ROWS_UPDATED -> viewModel.onRowsUpdated(data.value);
            }
        }

        @Override
        public void linkModel(ViewModel viewModel) {
            this.viewModel = viewModel;
            this.viewModel.onLinkModel(this);
        }

        @Override
        public int getCount() {
            return taskListProvider.getCount();
        }

        @Override
        public ResponseData getData(int index) {
            return taskListProvider.getValue(index);
        }
    }
}
