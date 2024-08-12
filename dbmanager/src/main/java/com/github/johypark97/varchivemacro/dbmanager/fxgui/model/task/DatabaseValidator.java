package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.task;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DatabaseValidator implements Supplier<String> {
    private final Gson gson = newGsonBuilder_dump().create();

    public final DlcSongManager dlcSongManager;

    public DatabaseValidator(DlcSongManager dlcSongManager) {
        this.dlcSongManager = dlcSongManager;
    }

    private void validate_dlcCode_songs_dlcs(StringBuilder builder) {
        Set<String> codeSetFromDlcsJson = dlcSongManager.getDlcCodeSet();
        Set<String> codeSetFromSongsJson =
                dlcSongManager.getSongList().stream().map((x) -> x.dlcCode)
                        .collect(Collectors.toSet());

        builder.append("dlcCode (songs.json - dlcs.json)\n");
        if (codeSetFromDlcsJson.equals(codeSetFromSongsJson)) {
            builder.append("- ok\n\n");
        } else {
            builder.append("- failed\nsongs.json: ")
                    .append(Sets.difference(codeSetFromSongsJson, codeSetFromDlcsJson))
                    .append("\ndlcs.json: ")
                    .append(Sets.difference(codeSetFromDlcsJson, codeSetFromSongsJson))
                    .append("\n\n");
        }
    }

    private void print_dlcCodeList(StringBuilder builder) {
        List<String> list = dlcSongManager.getDlcCodeList();

        builder.append("dlcCode list in priority order\n").append(gson.toJson(list)).append("\n\n");
    }

    private void validate_dlcCode_tabs_dlcs(StringBuilder builder) {
        Set<String> codeSetFromDlcsJson = dlcSongManager.getDlcCodeSet();
        Set<String> codeSetFromTabsJson =
                dlcSongManager.getDlcTabCodeMap().values().stream().flatMap(Set::stream)
                        .collect(Collectors.toSet());

        builder.append("dlcCode (tabs.json - dlcs.json)\n");
        if (codeSetFromDlcsJson.equals(codeSetFromTabsJson)) {
            builder.append("- ok\n\n");
        } else {
            builder.append("- failed\ntabs.json: ")
                    .append(Sets.difference(codeSetFromTabsJson, codeSetFromDlcsJson))
                    .append("\ndlcs.json: ")
                    .append(Sets.difference(codeSetFromDlcsJson, codeSetFromTabsJson))
                    .append("\n\n");
        }
    }

    private void print_dlcTabList(StringBuilder builder) {
        List<String> list = dlcSongManager.getDlcTabList();

        builder.append("dlcTab list in priority order\n").append(gson.toJson(list)).append("\n\n");
    }

    private void print_dlcCodeNameMap(StringBuilder builder) {
        Map<String, String> dlcCodeNameMap = dlcSongManager.getDlcCodeNameMap();

        builder.append("dlcCode - dlcName\n").append(gson.toJson(dlcCodeNameMap)).append("\n\n");
    }

    private void print_dlcTabCodeMap(StringBuilder builder) {
        Map<String, Set<String>> dlcTabCodeMap = dlcSongManager.getDlcTabCodeMap();

        builder.append("dlcTab - dlcCode\n").append(gson.toJson(dlcTabCodeMap)).append("\n\n");
    }

    @Override
    public String get() {
        StringBuilder builder = new StringBuilder(128);

        builder.append("-------- validation --------\n\n");

        validate_dlcCode_songs_dlcs(builder);
        print_dlcCodeList(builder);

        validate_dlcCode_tabs_dlcs(builder);
        print_dlcTabList(builder);

        builder.append("-------- key relation map --------\n\n");

        print_dlcCodeNameMap(builder);
        print_dlcTabCodeMap(builder);

        return builder.toString();
    }
}
