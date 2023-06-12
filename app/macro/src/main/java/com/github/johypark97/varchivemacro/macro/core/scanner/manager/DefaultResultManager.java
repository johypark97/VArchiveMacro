package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.protocol.Observers.Observable;
import com.github.johypark97.varchivemacro.lib.common.protocol.Observers.Observer;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.SongRecordManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.AnalyzedData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.Event;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.Event.Type;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResultListProvider;
import com.google.common.collect.Table.Cell;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultResultManager implements ResultManager, Observable<Event>, ResultListProvider {
    private final List<Observer<Event>> observerList = new CopyOnWriteArrayList<>();
    private final List<ResultData> resultDataList = new CopyOnWriteArrayList<>();

    private SongRecordManager songRecordManager;

    public void setModels(SongRecordManager songRecordManager) {
        this.songRecordManager = songRecordManager;
    }

    public ResultListProvider getResultListProvider() {
        return this;
    }

    private float parseRateText(String text) {
        int index = text.indexOf('%');
        if (index == -1) {
            return -1;
        }

        try {
            String s = text.substring(0, index);
            float value = Float.parseFloat(s);
            return (value >= 0 && value <= 100) ? value : -1;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public Iterator<ResultData> iterator() {
        return resultDataList.iterator();
    }

    @Override
    public void addObserver(Observer<Event> observer) {
        observerList.add(observer);
    }

    @Override
    public void deleteObservers() {
        observerList.clear();
    }

    @Override
    public void deleteObservers(Observer<Event> observer) {
        observerList.removeIf((x) -> x.equals(observer));
    }

    @Override
    public void notifyObservers(Event argument) {
        observerList.forEach((x) -> x.onNotifyObservers(argument));
    }

    @Override
    public ResponseData getValue(int index) {
        ResultData resultData = resultDataList.get(index);

        ResponseData data = new ResponseData();
        data.button = Button.valueOf(resultData.getNewRecord().button);
        data.composer = resultData.getSong().composer();
        data.dlc = resultData.getSong().dlc();
        data.isSelected = resultData.isSelected();
        data.newMaxCombo = resultData.getNewRecord().maxCombo;
        data.newRate = resultData.getNewRecord().rate;
        data.oldMaxCombo = resultData.getOldMaxCombo();
        data.oldRate = resultData.getOldRate();
        data.pattern = Pattern.valueOf(resultData.getNewRecord().pattern);
        data.resultNumber = resultData.getResultNumber();
        data.status = resultData.getStatus();
        data.taskNumber = resultData.getTaskNumber();
        data.title = resultData.getSong().title();

        return data;
    }

    @Override
    public int getCount() {
        return resultDataList.size();
    }

    @Override
    public void updateSelected(int index, boolean value) {
        ResultData data = resultDataList.get(index);
        if (data != null) {
            data.updateSelected(value);
        }
    }

    @Override
    public void clear() {
        resultDataList.clear();
        notifyObservers(new Event(Type.DATA_CHANGED));
    }

    @Override
    public void addAll(TaskManager taskManager) {
        for (TaskData taskData : taskManager) {
            LocalSong song = taskData.getSong();

            for (Cell<Button, Pattern, AnalyzedData> cell : taskData) {
                AnalyzedData analyzedData = cell.getValue();

                float rate = parseRateText(analyzedData.getRateText());
                if (rate == -1) {
                    continue;
                }

                Api.Button button = cell.getRowKey().toApi();
                Api.Pattern pattern = cell.getColumnKey().toApi();
                boolean maxCombo = analyzedData.isMaxCombo();

                LocalRecord newRecord = new LocalRecord(song.id(), button, pattern, rate, maxCombo);
                LocalRecord oldRecord = songRecordManager.findSameRecord(newRecord);

                if (oldRecord != null && oldRecord.isUpdated(newRecord)) {
                    int resultNumber = resultDataList.size();

                    ResultData data =
                            new DefaultResultData(resultNumber, taskData, oldRecord, newRecord);
                    resultDataList.add(data);
                }
            }
        }

        notifyObservers(new Event(Type.DATA_CHANGED));
    }

    private class DefaultResultData implements ResultData {
        private final LocalRecord newRecord;
        private final LocalSong song;
        private final boolean oldMaxCombo;
        private final float oldRate;
        private final int resultNumber;
        private final int taskNumber;

        private ResultStatus status = ResultStatus.NOT_UPLOADED;
        private boolean selected = true;

        public DefaultResultData(int resultNumber, TaskData taskData, LocalRecord oldRecord,
                LocalRecord newRecord) {
            this.newRecord = newRecord;
            this.resultNumber = resultNumber;

            oldMaxCombo = oldRecord.maxCombo;
            oldRate = oldRecord.rate;
            song = taskData.getSong();
            taskNumber = taskData.getTaskNumber();
        }

        @Override
        public LocalRecord getNewRecord() {
            return newRecord;
        }

        @Override
        public LocalSong getSong() {
            return song;
        }

        @Override
        public boolean getOldMaxCombo() {
            return oldMaxCombo;
        }

        @Override
        public float getOldRate() {
            return oldRate;
        }

        @Override
        public int getResultNumber() {
            return resultNumber;
        }

        @Override
        public int getTaskNumber() {
            return taskNumber;
        }

        @Override
        public ResultStatus getStatus() {
            return status;
        }

        @Override
        public void setStatus(ResultStatus value) {
            status = value;
            notifyObservers(new Event(Type.ROWS_UPDATED, resultNumber));
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void updateSelected(boolean value) {
            selected = value;
            notifyObservers(new Event(Type.ROWS_UPDATED, resultNumber));
        }
    }
}
