package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.DlcData;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.util.LocalSongComparator;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SongManager {
    private static final String DLC_COLLABORATION_TAB = "COLLABORATION";

    private final DlcData dlcData;
    private final Map<Integer, LocalSong> songs;

    public SongManager(Path songDataPath, Path dlcDataPath) throws IOException {
        dlcData = DlcData.loadJson(dlcDataPath);
        songs = LocalSong.loadJson(songDataPath);
    }

    public List<LocalSong> getSongs() {
        return songs.values().stream().sorted(new LocalSongComparator()).toList();
    }

    public Map<String, String> getDlcCodeNameMap() {
        Map<String, String> map = new LinkedHashMap<>();
        dlcData.dlcs.forEach((key, info) -> map.put(info.code(), info.name()));
        return map;
    }

    public Map<String, List<LocalSong>> getTabSongMap() {
        Map<String, List<LocalSong>> map = new LinkedHashMap<>();
        dlcData.dlcs.forEach((key, value) -> {
            String tab = value.tab();
            String code = value.code();

            List<LocalSong> list = map.computeIfAbsent(tab, x -> new ArrayList<>());
            songs.values().stream().filter((song) -> song.dlcCode().equals(code))
                    .forEach(list::add);
        });

        map.forEach((key, value) -> {
            if (!DLC_COLLABORATION_TAB.equals(key)) {
                value.sort(new LocalSongComparator());
            } else {
                value.sort(new LocalSongComparator() {
                    @Serial
                    private static final long serialVersionUID = -217358984531426514L;

                    @Override
                    public int compare(LocalSong o1, LocalSong o2) {
                        int left = getDlcOrder(o1.dlcCode());
                        int right = getDlcOrder(o2.dlcCode());

                        int diff = left - right;
                        if (diff != 0) {
                            return diff;
                        }

                        return super.compare(o1, o2);
                    }
                });
            }
        });

        return map;
    }

    public Map<String, List<LocalSong>> getTabSongMap(Set<String> ownedDlcs) {
        Map<String, List<LocalSong>> map = getTabSongMap();
        map.values().forEach((x) -> x.removeIf((localSong) -> !isUnlocked(localSong, ownedDlcs)));
        return map;
    }

    protected int getDlcOrder(String dlcCode) {
        return dlcData.dlcs.entrySet().parallelStream()
                .filter((x) -> x.getValue().code().equals(dlcCode)).map(Entry::getKey).findAny()
                .orElse(-1);
    }

    protected boolean isUnlocked(LocalSong localSong, Set<String> ownedDlcs) {
        List<Set<String>> conditions = dlcData.unlocks.get(localSong.id());
        if (conditions == null) {
            return ownedDlcs.contains(localSong.dlcCode());
        }

        for (Set<String> condition : conditions) {
            if (ownedDlcs.containsAll(condition)) {
                return true;
            }
        }

        return false;
    }
}
