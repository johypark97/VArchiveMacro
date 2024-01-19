package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import javafx.collections.transformation.FilteredList;

public interface DatabaseModel {
    void load(Path path) throws IOException;

    FilteredList<SongData> getFilteredSongList();

    void updateFilteredSongListFilter(String regex, SongDataProperty property);

    void validateDatabase(Consumer<String> onDone);

    void compareDatabaseWithRemote(Consumer<String> onDone, Consumer<Throwable> onThrow);

    List<LocalDlcSong> getDlcSongList();

    TitleTool getTitleTool();
}
