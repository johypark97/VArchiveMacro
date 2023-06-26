package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseValidator {
    private final Gson gson = newGsonBuilder_dump().create();
    private final SongModel songModel;

    public DatabaseValidator(SongModel songModel) {
        this.songModel = songModel;
    }

    public String validate() {
        StringBuilder builder = new StringBuilder();

        List<LocalSong> songs = songModel.getSongList();
        Set<String> dlcCodeSet = songModel.getDlcCodeSet();
        Set<String> dlcTabSet = songModel.getDlcTabSet();

        builder.append("-------- validation --------\n");
        builder.append('\n');

        builder.append("dlcCode (songs.json - dlcs.json)\n");
        {
            Set<String> set = songs.stream().map(LocalSong::dlcCode).collect(Collectors.toSet());
            if (set.equals(dlcCodeSet)) {
                builder.append("- ok\n");
            } else {
                builder.append("- failed\n");
                builder.append("songs.json: ").append(Sets.difference(set, dlcCodeSet))
                        .append('\n');
                builder.append("dlcs.json: ").append(Sets.difference(dlcCodeSet, set)).append('\n');
            }
        }
        builder.append('\n');

        builder.append("dlcCode list\n");
        {
            List<String> list = songModel.getDlcCodeList();
            builder.append(gson.toJson(list)).append('\n');
        }
        builder.append('\n');

        builder.append("dlcTab (songs.json - tabs.json)\n");
        {
            Set<String> set = songs.stream().map(LocalSong::dlcTab).collect(Collectors.toSet());
            if (set.equals(dlcTabSet)) {
                builder.append("- ok\n");
            } else {
                builder.append("- failed\n");
                builder.append("songs.json: ").append(Sets.difference(set, dlcTabSet)).append('\n');
                builder.append("tabs.json: ").append(Sets.difference(dlcTabSet, set)).append('\n');
            }
        }
        builder.append('\n');

        builder.append("dlcTab list\n");
        {
            List<String> list = songModel.getDlcTabList();
            builder.append(gson.toJson(list)).append('\n');
        }
        builder.append('\n');

        builder.append("-------- songs.json data check --------\n");
        builder.append('\n');

        builder.append("dlc (pack names) - dlcCode\n");
        {
            Map<String, Set<String>> map = new HashMap<>();
            songs.forEach((song) -> map.computeIfAbsent(song.dlc(), (x) -> new HashSet<>())
                    .add(song.dlcCode()));
            List<String> list =
                    map.entrySet().stream().map((x) -> x.getKey() + " - " + x.getValue()).sorted()
                            .toList();
            builder.append(gson.toJson(list)).append('\n');
        }
        builder.append('\n');

        builder.append("dlcTab - dlcCode\n");
        {
            Map<String, Set<String>> map = songModel.getDlcTabCodeMap();
            List<String> list =
                    map.entrySet().stream().map((x) -> x.getKey() + " - " + x.getValue()).sorted()
                            .toList();
            builder.append(gson.toJson(list)).append('\n');
        }
        builder.append('\n');

        return builder.toString();
    }
}
