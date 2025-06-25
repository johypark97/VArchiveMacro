package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.converter.RecordManagerConverter;
import java.util.List;

public class RemoteSongRecordLoadService implements SongRecordLoadService {
    private final String djName;

    public RemoteSongRecordLoadService(String djName) {
        this.djName = djName;
    }

    @Override
    public List<SongRecordTable> load() throws Exception {
        return RecordManagerConverter.toSongRecordTableList(new DefaultRecordManager(djName));
    }
}
