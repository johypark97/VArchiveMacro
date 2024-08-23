package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongWrapper;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.task.RemoteValidator;
import com.github.johypark97.varchivemacro.lib.scanner.database.CachedReadOnlySongDatabase;
import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultTitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DefaultDatabaseModel implements DatabaseModel {
    private static final String SONG_DATABASE_FILE_NAME = "song.db";
    private static final String TITLE_FILE_NAME = "titles.json";

    private SongDatabase songDatabase;
    private TitleTool titleTool;

    public ObservableList<SongWrapper> observableSongWrapperList;

    @Override
    public void load(Path path) throws IOException, SQLException {
        Path songDatabasePath = path.resolve(SONG_DATABASE_FILE_NAME);
        Path titlePath = path.resolve(TITLE_FILE_NAME);

        songDatabase = new CachedReadOnlySongDatabase(songDatabasePath);
        titleTool = new DefaultTitleTool(titlePath);
    }

    @Override
    public ObservableList<SongWrapper> getObservableSongWrapperList() {
        if (observableSongWrapperList == null) {
            observableSongWrapperList = songDatabase.songList().stream().map(SongWrapper::new)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

        return observableSongWrapperList;
    }

    @Override
    public void compareDatabaseWithRemote(Consumer<String> onDone, Consumer<Throwable> onThrow) {
        CompletableFuture.supplyAsync(new RemoteValidator(songDatabase))
                .orTimeout(4, TimeUnit.SECONDS).exceptionally(x -> {
                    Platform.runLater(() -> onThrow.accept(x));
                    return "Error";
                }).thenAccept(x -> Platform.runLater(() -> onDone.accept(x)));
    }

    @Override
    public List<Song> getSongList() {
        return songDatabase.songList();
    }

    @Override
    public TitleTool getTitleTool() {
        return titleTool;
    }
}
