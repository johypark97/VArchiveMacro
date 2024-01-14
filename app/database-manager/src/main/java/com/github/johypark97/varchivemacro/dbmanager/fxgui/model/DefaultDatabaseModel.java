package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.lib.common.database.DefaultDlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.DefaultTitleTool;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;

public class DefaultDatabaseModel implements DatabaseModel {
    private static final String DLC_FILENAME = "dlcs.json";
    private static final String SONG_FILENAME = "songs.json";
    private static final String TAB_FILENAME = "tabs.json";
    private static final String TITLE_FILENAME = "titles.json";

    private DlcSongManager dlcSongManager;
    private TitleTool titleTool;

    public FilteredList<SongData> filteredSongList;

    @Override
    public void load(Path path) throws IOException {
        Path dlcPath = path.resolve(DLC_FILENAME);
        Path songPath = path.resolve(SONG_FILENAME);
        Path tabPath = path.resolve(TAB_FILENAME);
        Path titlePath = path.resolve(TITLE_FILENAME);

        dlcSongManager = new DefaultDlcSongManager(songPath, dlcPath, tabPath);
        titleTool = new DefaultTitleTool(titlePath);
    }

    @Override
    public FilteredList<SongData> getFilteredSongList() {
        if (filteredSongList == null) {
            filteredSongList = new FilteredList<>(dlcSongManager.getDlcSongList().stream()
                    .map(x -> new SongData(x, dlcSongManager.getDlcCodeNameMap()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }

        return filteredSongList;
    }

    @Override
    public void updateFilteredSongListFilter(String regex, SongDataProperty property) {
        if (filteredSongList == null) {
            return;
        }

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Function<SongData, String> valueGetter = switch (property) {
            case ID -> x -> String.valueOf(x.getId());
            case TITLE -> SongData::getTitle;
            case REMOTE_TITLE -> SongData::getRemoteTitle;
            case COMPOSER -> SongData::getComposer;
            case DLC -> SongData::getDlc;
            case PRIORITY -> x -> String.valueOf(x.getPriority());
        };

        filteredSongList.setPredicate(x -> pattern.matcher(valueGetter.apply(x)).find());
    }
}
