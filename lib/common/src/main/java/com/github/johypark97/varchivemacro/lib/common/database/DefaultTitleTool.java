package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.SongManager.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.TitleData;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DefaultTitleTool implements TitleTool {
    private final Map<Integer, String> shortTitleMap = new HashMap<>();
    private final String titleChars;

    public DefaultTitleTool(Path path) throws IOException {
        TitleData titleData = TitleData.loadJson(path);

        titleChars = titleData.titleChars;
        titleData.shortTitles.forEach((x) -> shortTitleMap.put(x.id(), x.value()));
    }

    @Override
    public String getTitleChars() {
        return titleChars;
    }

    @Override
    public boolean hasShortTitle(LocalSong song) {
        return shortTitleMap.containsKey(song.id);
    }

    @Override
    public String getShortTitle(LocalSong song) {
        return shortTitleMap.getOrDefault(song.id, song.title);
    }
}
