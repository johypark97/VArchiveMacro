package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.lib.common.database.DefaultDlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.DefaultTitleTool;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;

public class DefaultDatabaseModel implements DatabaseModel {
    private static final String DLC_FILENAME = "dlcs.json";
    private static final String SONG_FILENAME = "songs.json";
    private static final String TAB_FILENAME = "tabs.json";
    private static final String TITLE_FILENAME = "titles.json";

    private DlcSongManager dlcSongManager;
    private TitleTool titleTool;

    @Override
    public void load(Path path) throws IOException {
        Path dlcPath = path.resolve(DLC_FILENAME);
        Path songPath = path.resolve(SONG_FILENAME);
        Path tabPath = path.resolve(TAB_FILENAME);
        Path titlePath = path.resolve(TITLE_FILENAME);

        dlcSongManager = new DefaultDlcSongManager(songPath, dlcPath, tabPath);
        titleTool = new DefaultTitleTool(titlePath);
    }
}
