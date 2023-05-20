package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScannerTaskStatus;

public interface ScannerTaskModel {
    class Event {
        public enum Type {DATA_CHANGED, ROWS_INSERTED, ROWS_UPDATED}


        public final Type type;
        public final int value;

        public Event(Type type, int value) {
            this.type = type;
            this.value = value;
        }

        public Event(Type type) {
            this(type, 0);
        }
    }


    interface TaskServer {
        ResponseData getValue(int index);

        int getCount();
    }


    class ResponseData {
        public ScannerTaskStatus status;
        public String composer;
        public String dlc;
        public String tab;
        public String title;
        public int count;
        public int index;
        public int taskNumber;
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


    class TaskModel implements Client<Event, TaskServer>, Model {
        private TaskServer taskServer;
        private ViewModel viewModel;

        @Override
        public void onAddClient(TaskServer channel) {
            taskServer = channel;
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
            return taskServer.getCount();
        }

        @Override
        public ResponseData getData(int index) {
            return taskServer.getValue(index);
        }
    }
}
