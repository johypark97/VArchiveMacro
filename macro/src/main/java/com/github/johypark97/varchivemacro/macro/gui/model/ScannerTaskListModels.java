package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.lib.common.protocol.Observers.Observer;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskStatus;

public interface ScannerTaskListModels {
    interface TaskListProvider {
        ResponseData getValue(int index);

        int getCount();

        void updateSelected(int index, boolean value);
    }


    interface Model {
        void linkModel(ViewModel viewModel);

        int getCount();

        ResponseData getData(int index);

        void updateSelected(int index, boolean value);
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
        public String composer = "";
        public String dlc = "";
        public String scannedTitle = "";
        public String tab = "";
        public String title = "";
        public TaskStatus status = TaskStatus.NONE;
        public boolean selected;
        public float accuracy;
        public int distance;
        public int taskNumber;
    }


    class ScannerTaskListModel implements Observer<Event>, Model {
        private TaskListProvider taskListProvider;
        private ViewModel viewModel;

        public void linkTaskListProvider(TaskListProvider provider) {
            taskListProvider = provider;
        }

        @Override
        public void onNotifyObservers(Event argument) {
            switch (argument.type) {
                case DATA_CHANGED -> viewModel.onDataChanged();
                case ROWS_INSERTED -> viewModel.onRowsInserted(argument.value);
                case ROWS_UPDATED -> viewModel.onRowsUpdated(argument.value);
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

        @Override
        public void updateSelected(int index, boolean value) {
            taskListProvider.updateSelected(index, value);
        }
    }
}