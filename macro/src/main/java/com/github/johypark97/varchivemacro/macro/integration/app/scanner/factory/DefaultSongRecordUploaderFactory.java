package com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory;

import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.loader.AccountFileLoader;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.Account;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.uploader.SongRecordUploader;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import java.nio.file.Path;

public class DefaultSongRecordUploaderFactory implements SongRecordUploaderFactory {
    private final SongTitleService songTitleService;

    public DefaultSongRecordUploaderFactory(SongTitleService songTitleService) {
        this.songTitleService = songTitleService;
    }

    @Override
    public SongRecordUploader create(String accountFile) throws Exception {
        Path path = PathValidator.validateAndConvert(accountFile);
        Account account = new AccountFileLoader(path).load();
        return new SongRecordUploader(songTitleService, account);
    }
}
