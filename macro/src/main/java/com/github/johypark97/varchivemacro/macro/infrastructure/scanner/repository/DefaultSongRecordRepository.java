package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository;

import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.converter.RecordButtonConverter;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.converter.RecordPatternConverter;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader.SongRecordRepositoryLoader;
import java.io.IOException;

public class DefaultSongRecordRepository implements SongRecordRepository {
    private final SongRecordRepositoryLoader loader;

    private DefaultRecordManager recordManager;

    public DefaultSongRecordRepository(SongRecordRepositoryLoader loader) {
        this.loader = loader;
    }

    @Override
    public void load() {
        try {
            recordManager = loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush() {
        try {
            recordManager.saveJson(loader.getSavePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(SongRecordTable value) {
        value.recordStream().forEach(cell -> {
            Enums.Button button = RecordButtonConverter.toLib(cell.button());
            Enums.Pattern pattern = RecordPatternConverter.toLib(cell.pattern());

            recordManager.updateRecord(new RecordManager.LocalRecord(value.songId, button, pattern,
                    cell.record().rate(), cell.record().maxCombo()));
        });
    }

    @Override
    public SongRecordTable findById(int songId) {
        SongRecordTable table = new SongRecordTable(songId);

        recordManager.getRecord(songId).forEach((button, patternLocalRecordMap) -> {
            RecordButton domainButton = RecordButtonConverter.toDomain(button);

            patternLocalRecordMap.forEach((pattern, localRecord) -> {
                RecordPattern domainPattern = RecordPatternConverter.toDomain(pattern);

                table.setSongRecord(domainButton, domainPattern,
                        new SongRecord(localRecord.rate, localRecord.maxCombo));
            });
        });

        return table;
    }
}
