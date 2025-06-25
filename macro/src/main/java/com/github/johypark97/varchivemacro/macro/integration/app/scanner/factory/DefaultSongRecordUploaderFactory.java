package com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory;

import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.loader.AccountFileLoader;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.Account;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.uploader.SongRecordUploader;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.infra.SongTitleMapper;
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
