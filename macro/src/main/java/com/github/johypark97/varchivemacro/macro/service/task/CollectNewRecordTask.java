package com.github.johypark97.varchivemacro.macro.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.RecordData;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.AnalysisDataRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.NewRecordDataRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.record.repository.RecordRepository;
import com.google.common.collect.Table.Cell;

public class CollectNewRecordTask extends InterruptibleTask<Void> {
    private final AnalysisDataRepository analysisDataRepository;
    private final NewRecordDataRepository newRecordDataRepository;
    private final RecordRepository recordRepository;

    public CollectNewRecordTask(AnalysisDataRepository analysisDataRepository,
            NewRecordDataRepository newRecordDataRepository, RecordRepository recordRepository) {
        this.analysisDataRepository = analysisDataRepository;
        this.newRecordDataRepository = newRecordDataRepository;
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
        if (analysisDataRepository.isEmpty()) {
            throw new IllegalStateException("AnalysisDataRepository is empty");
        }

        newRecordDataRepository.clear();

        for (AnalysisData data : analysisDataRepository.copyAnalysisDataList()) {
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
                    newRecordDataRepository.createNewRecordData(song, nullRecord, newRecord);
                } else if (previousRecord.isUpdated(newRecord)) {
                    newRecordDataRepository.createNewRecordData(song, previousRecord, newRecord);
                }
            }
        }

        return null;
    }
}
