package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordButtonConverter;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordPatternConverter;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSongRecordLoader implements SongRecordLoader {
    protected List<SongRecordTable> convertRecord(RecordManager recordManager) {
        Map<Integer, SongRecordTable> map = new HashMap<>();

        recordManager.forEach((songId, buttonMap) -> {
            SongRecordTable table = map.computeIfAbsent(songId, x -> new SongRecordTable(songId));

            buttonMap.forEach((button, patternMap) -> patternMap.forEach((pattern, localRecord) -> {
                RecordButton b = RecordButtonConverter.toDomain(button);
                RecordPattern p = RecordPatternConverter.toDomain(pattern);

                table.setSongRecord(b, p, new SongRecord(localRecord.rate, localRecord.maxCombo));
            }));
        });

        return map.values().stream().toList();
    }
}
