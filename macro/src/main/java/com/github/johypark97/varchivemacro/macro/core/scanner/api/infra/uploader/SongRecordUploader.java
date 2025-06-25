package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.uploader;

import com.github.johypark97.varchivemacro.lib.scanner.api.Api;
import com.github.johypark97.varchivemacro.lib.scanner.api.ApiException;
import com.github.johypark97.varchivemacro.lib.scanner.api.RecordUploader;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordButtonConverter;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordPatternConverter;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.Account;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.infra.SongTitleMapper;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class SongRecordUploader {
    private final RecordUploader uploader;
    private final SongTitleMapper songTitleMapper;

    public SongRecordUploader(Account account, SongTitleMapper songTitleMapper)
            throws GeneralSecurityException {
        this.songTitleMapper = songTitleMapper;

        uploader = Api.newRecordUploader(account.userNo(), account.token());
    }

    public boolean upload(Song song, RecordButton button, RecordPattern pattern, SongRecord record,
            boolean includeComposer) throws IOException, InterruptedException, ApiException {
        RecordUploader.RequestJson json =
                new RecordUploader.RequestJson(songTitleMapper.getRemoteTitleOrDefault(song),
                        RecordButtonConverter.toLib(button), RecordPatternConverter.toLib(pattern),
                        record.rate(), record.maxCombo());

        if (includeComposer) {
            json.composer = song.composer();
        }

        uploader.upload(json);

        return uploader.getResult();
    }
}
