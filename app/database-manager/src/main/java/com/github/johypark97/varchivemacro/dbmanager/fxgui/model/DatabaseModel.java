package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongProperty;
import java.io.IOException;
import java.nio.file.Path;
import javafx.collections.transformation.FilteredList;

public interface DatabaseModel {
    void load(Path path) throws IOException;

    FilteredList<SongData> getFilteredSongList();

    void updateFilteredSongListFilter(String regex, SongProperty property);
}
