package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.core.scanner.ResultManager.RecordData.Result;

public interface ScannerResultListModels {
    class Event {
        public enum Type {DATA_CHANGED, ROWS_UPDATED}


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


    class ResponseData {
        public Button button;
        public Pattern pattern;
        public Result status;
        public String composer;
        public String dlc;
        public String title;
        public boolean isSelected;
        public boolean newMaxCombo;
        public boolean oldMaxCombo;
        public float newRate;
        public float oldRate;
        public int resultNumber;
        public int taskNumber;
    }


    interface ResultListProvider {
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

        void onRowsUpdated(int row);
    }


    class ScannerResultListModel implements Client<Event, ResultListProvider>, Model {
        private ResultListProvider resultListProvider;
        private ViewModel viewModel;

        @Override
        public void onAddClient(ResultListProvider channel) {
            resultListProvider = channel;
        }

        @Override
        public void onNotify(Event data) {
            switch (data.type) {
                case DATA_CHANGED -> viewModel.onDataChanged();
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
            return resultListProvider.getCount();
        }

        @Override
        public ResponseData getData(int index) {
            return resultListProvider.getValue(index);
        }

        @Override
        public void updateSelected(int index, boolean value) {
            resultListProvider.updateSelected(index, value);
        }
    }
}
