package com.github.johypark97.varchivemacro.macro.integration.context;

import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigStorageService;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.common.config.infra.repository.DefaultConfigRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.app.CaptureRegionService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.SongRecordService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.SongRecordStorageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.repository.DefaultSongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongStorageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.repository.DefaultSongRepository;
import com.github.johypark97.varchivemacro.macro.integration.app.service.WebBrowserService;
import java.nio.file.Path;
import javafx.application.HostServices;

public class GlobalContext implements Context {
    // constants
    private final Path CONFIG_FILE_PATH = Path.of("config.json");
    private final Path RECORD_FILE_PATH = Path.of("records.json");
    private final Path SONG_DATABASE_FILE_PATH = Path.of("data/song.db");

    // repositories
    final ConfigRepository configRepository = new DefaultConfigRepository();
    final SongRecordRepository songRecordRepository = new DefaultSongRecordRepository();
    final SongRepository songRepository = new DefaultSongRepository();

    // services
    public final CaptureRegionService captureRegionService = new CaptureRegionService();

    public final ConfigService configService = new ConfigService(configRepository);
    public final ConfigStorageService configStorageService =
            new ConfigStorageService(configRepository, CONFIG_FILE_PATH);

    public final SongRecordService songRecordService = new SongRecordService(songRecordRepository);
    public final SongRecordStorageService songRecordStorageService =
            new SongRecordStorageService(songRecordRepository, RECORD_FILE_PATH);

    public final SongService songService = new SongService(songRepository);
    public final SongStorageService songStorageService =
            new SongStorageService(songRepository, SONG_DATABASE_FILE_PATH);

    public final WebBrowserService webBrowserService;

    public GlobalContext(HostServices hostServices) {
        webBrowserService = new WebBrowserService(hostServices);
    }
}
