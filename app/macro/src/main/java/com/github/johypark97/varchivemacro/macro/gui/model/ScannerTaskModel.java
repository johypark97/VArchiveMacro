package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Requester;
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


    interface Request {
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


    class TaskModel implements Client<Event, Object, Request>, Model {
        private Requester<Object, Request> requester;
        private ViewModel viewModel;

        @Override
        public void onAddClient(Requester<Object, Request> requester) {
            this.requester = requester;
        }

        @Override
        public void onNotify(Event e) {
            switch (e.type) {
                case DATA_CHANGED -> viewModel.onDataChanged();
                case ROWS_INSERTED -> viewModel.onRowsInserted(e.value);
                case ROWS_UPDATED -> viewModel.onRowsUpdated(e.value);
            }
        }

        @Override
        public void linkModel(ViewModel viewModel) {
            this.viewModel = viewModel;
            this.viewModel.onLinkModel(this);
        }

        @Override
        public int getCount() {
            return requester.request(null).getCount();
        }

        @Override
        public ResponseData getData(int index) {
            return requester.request(null).getValue(index);
        }
    }
}
