package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.task.DatabaseValidator;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.task.RemoteValidator;
import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultDlcSongManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultTitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javafx.application.Platform;

public class DefaultDatabaseModel implements DatabaseModel {
    private static final String DLC_FILENAME = "dlcs.json";
    private static final String SONG_FILENAME = "songs.json";
    private static final String TAB_FILENAME = "tabs.json";
    private static final String TITLE_FILENAME = "titles.json";

    private DlcSongManager dlcSongManager;
    private List<SongData> songDataList;
    private TitleTool titleTool;

    @Override
    public void load(Path path) throws IOException {
        Path dlcPath = path.resolve(DLC_FILENAME);
        Path songPath = path.resolve(SONG_FILENAME);
        Path tabPath = path.resolve(TAB_FILENAME);
        Path titlePath = path.resolve(TITLE_FILENAME);

        dlcSongManager = DefaultDlcSongManager.load(songPath, dlcPath, tabPath);
        titleTool = new DefaultTitleTool(titlePath);
    }

    @Override
    public List<SongData> getSongDataList() {
        if (songDataList == null) {
            songDataList = dlcSongManager.getDlcSongList().stream()
                    .map(x -> new SongData(x, dlcSongManager.getDlcCodeNameMap())).toList();
        }

        return List.copyOf(songDataList);
    }

    @Override
    public void validateDatabase(Consumer<String> onDone) {
        CompletableFuture.supplyAsync(new DatabaseValidator(dlcSongManager))
                .thenAccept(x -> Platform.runLater(() -> onDone.accept(x)));
    }

    @Override
    public void compareDatabaseWithRemote(Consumer<String> onDone, Consumer<Throwable> onThrow) {
        CompletableFuture.supplyAsync(new RemoteValidator(dlcSongManager))
                .orTimeout(4, TimeUnit.SECONDS).exceptionally(x -> {
                    Platform.runLater(() -> onThrow.accept(x));
                    return "Error";
                }).thenAccept(x -> Platform.runLater(() -> onDone.accept(x)));
    }

    @Override
    public List<LocalDlcSong> getDlcSongList() {
        return dlcSongManager.getDlcSongList();
    }

    @Override
    public TitleTool getTitleTool() {
        return titleTool;
    }
}
