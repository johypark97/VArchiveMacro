package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface DatabaseModel {
    void load(Path path) throws IOException;

    List<SongData> getSongDataList();

    void validateDatabase(Consumer<String> onDone);

    void compareDatabaseWithRemote(Consumer<String> onDone, Consumer<Throwable> onThrow);

    List<LocalDlcSong> getDlcSongList();

    TitleTool getTitleTool();
}
