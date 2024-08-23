package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import javafx.collections.ObservableList;

public interface DatabaseModel {
    void load(Path path) throws IOException, SQLException;

    ObservableList<SongWrapper> getObservableSongWrapperList();

    void compareDatabaseWithRemote(Consumer<String> onDone, Consumer<Throwable> onThrow);

    List<Song> getSongList();

    TitleTool getTitleTool();
}
