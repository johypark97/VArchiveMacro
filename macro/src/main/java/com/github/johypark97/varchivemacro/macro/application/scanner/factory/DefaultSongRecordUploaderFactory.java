package com.github.johypark97.varchivemacro.macro.application.scanner.factory;

import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.infrastructure.api.loader.AccountFileLoader;
import com.github.johypark97.varchivemacro.macro.infrastructure.api.model.Account;
import com.github.johypark97.varchivemacro.macro.infrastructure.api.uploader.SongRecordUploader;
import com.github.johypark97.varchivemacro.macro.infrastructure.songtitle.SongTitleMapper;
import java.nio.file.Path;

public class DefaultSongRecordUploaderFactory implements SongRecordUploaderFactory {
    private final SongTitleMapper songTitleMapper;

    public DefaultSongRecordUploaderFactory(SongTitleMapper songTitleMapper) {
        this.songTitleMapper = songTitleMapper;
    }

    @Override
    public SongRecordUploader create(String accountFile) throws Exception {
        Path path = PathValidator.validateAndConvert(accountFile);
        Account account = new AccountFileLoader(path).load();
        return new SongRecordUploader(account, songTitleMapper);
    }
}
