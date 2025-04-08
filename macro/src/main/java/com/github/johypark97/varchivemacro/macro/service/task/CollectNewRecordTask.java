package com.github.johypark97.varchivemacro.macro.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.domain.AnalysisDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.NewRecordDataDomain;
import com.github.johypark97.varchivemacro.macro.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.model.RecordData;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;
import com.google.common.collect.Table.Cell;

public class CollectNewRecordTask extends InterruptibleTask<Void> {
    private final RecordRepository recordRepository;

    private final AnalysisDataDomain analysisDataDomain;
    private final NewRecordDataDomain newRecordDataDomain;

    public CollectNewRecordTask(RecordRepository recordRepository,
            AnalysisDataDomain analysisDataDomain, NewRecordDataDomain newRecordDataDomain) {
        this.analysisDataDomain = analysisDataDomain;
        this.newRecordDataDomain = newRecordDataDomain;
        this.recordRepository = recordRepository;
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
        if (analysisDataDomain.isEmpty()) {
            throw new IllegalStateException("AnalysisDataDomain is empty");
        }

        newRecordDataDomain.clear();

        for (AnalysisData data : analysisDataDomain.copyAnalysisDataList()) {
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
                LocalRecord previousRecord = recordRepository.findSameRecord(newRecord);

                if (previousRecord == null) {
                    LocalRecord nullRecord = LocalRecord.nullRecord(song.id(), button, pattern);
                    newRecordDataDomain.createNewRecordData(song, nullRecord, newRecord);
                } else if (previousRecord.isUpdated(newRecord)) {
                    newRecordDataDomain.createNewRecordData(song, previousRecord, newRecord);
                }
            }
        }

        return null;
    }
}
