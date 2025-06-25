package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import java.util.List;

public class RemoteSongRecordLoader extends AbstractSongRecordLoader {
    private final String djName;

    public RemoteSongRecordLoader(String djName) {
        this.djName = djName;
    }

    @Override
    public List<SongRecordTable> load() throws Exception {
        return convertRecord(new DefaultRecordManager(djName));
    }
}
