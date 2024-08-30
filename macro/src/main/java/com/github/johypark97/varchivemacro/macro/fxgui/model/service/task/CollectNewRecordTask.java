package com.github.johypark97.varchivemacro.macro.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.RecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager;
import com.google.common.collect.Table.Cell;
import java.lang.ref.WeakReference;

public class CollectNewRecordTask extends InterruptibleTask<Void> {
    private final WeakReference<AnalysisDataManager> analysisDataManagerReference;
    private final WeakReference<NewRecordDataManager> newRecordDataManagerReference;
    private final WeakReference<RecordModel> recordModelWeakReference;

    public CollectNewRecordTask(RecordModel recordModel, AnalysisDataManager analysisDataManager,
            NewRecordDataManager newRecordDataManager) {
        analysisDataManagerReference = new WeakReference<>(analysisDataManager);
        newRecordDataManagerReference = new WeakReference<>(newRecordDataManager);
        recordModelWeakReference = new WeakReference<>(recordModel);
    }

    private RecordModel getRecordModel() {
        return recordModelWeakReference.get();
    }

    private AnalysisDataManager getAnalysisDataManager() {
        return analysisDataManagerReference.get();
    }

    private NewRecordDataManager getNewRecordDataManager() {
        return newRecordDataManagerReference.get();
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
    protected Void callTask() throws Exception {
        // throw an exception if there is no analysis data
        if (getAnalysisDataManager().isEmpty()) {
            throw new IllegalStateException("AnalysisDataManager is empty");
        }

        getNewRecordDataManager().clear();

        for (AnalysisData data : getAnalysisDataManager().copyAnalysisDataList()) {
            Song song = data.songDataProperty().get().songProperty().get();

            for (Cell<Button, Pattern, RecordData> cell : data.recordDataTable.cellSet()) {
                float rate = parseRateText(cell.getValue().rateText.get());

                if (rate == -1) {
                    continue;
                }

                Button button = cell.getRowKey();
                Pattern pattern = cell.getColumnKey();
                boolean maxCombo = cell.getValue().maxCombo.get();

                LocalRecord newRecord = new LocalRecord(song.id(), button, pattern, rate, maxCombo);
                LocalRecord previousRecord = getRecordModel().findSameRecord(newRecord);

                if (previousRecord == null) {
                    LocalRecord nullRecord = LocalRecord.nullRecord(song.id(), button, pattern);
                    getNewRecordDataManager().createNewRecordData(song, nullRecord, newRecord);
                } else if (previousRecord.isUpdated(newRecord)) {
                    getNewRecordDataManager().createNewRecordData(song, previousRecord, newRecord);
                }
            }
        }

        return null;
    }
}
