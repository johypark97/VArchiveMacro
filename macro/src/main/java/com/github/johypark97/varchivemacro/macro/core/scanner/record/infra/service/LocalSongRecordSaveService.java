package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordButtonConverter;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordPatternConverter;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class LocalSongRecordSaveService {
    private final Path path;

    public LocalSongRecordSaveService(Path path) {
        this.path = path;
    }

    public void save(List<SongRecordTable> value) throws IOException {
        DefaultRecordManager recordManager = new DefaultRecordManager();

        value.forEach(songRecordTable -> songRecordTable.recordStream().forEach(cell -> {
            Enums.Button button = RecordButtonConverter.toLib(cell.button());
            Enums.Pattern pattern = RecordPatternConverter.toLib(cell.pattern());

            recordManager.updateRecord(
                    new RecordManager.LocalRecord(songRecordTable.songId, button, pattern,
                            cell.record().rate(), cell.record().maxCombo()));
        }));

        recordManager.saveJson(path);
    }
}
