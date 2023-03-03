package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.Dlc;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.Tab;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.Unlock;
import com.github.johypark97.varchivemacro.lib.common.database.util.LocalSongComparator;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DlcManager extends SongManager {
    private static final String DLC_COLLABORATION_TAB = "COLLABORATION";

    protected final Map<Integer, String> tabs;
    protected final Map<Integer, Unlock> unlocks;
    protected final Map<String, Dlc> dlcs;

    public DlcManager(Path songPath, Path dlcPath, Path tabPath, Path unlockPath)
            throws IOException {
        super(songPath);

        dlcs = Dlc.loadJson(dlcPath);
        tabs = Tab.loadJson(tabPath);
        unlocks = Unlock.loadJson(unlockPath);
    }

    public List<String> getDlcCodeList() {
        return dlcs.keySet().stream().toList();
    }

    public Set<String> getDlcCodeSet() {
        return dlcs.keySet();
    }

    public List<String> getDlcTabList() {
        return tabs.values().stream().toList();
    }

    public Set<String> getDlcTabSet() {
        return new HashSet<>(tabs.values());
    }

    public Map<String, String> getDlcCodeNameMap() {
        Function<Entry<String, Dlc>, String> keyMapper = Entry::getKey;
        Function<Entry<String, Dlc>, String> valueMapper = (x) -> x.getValue().name();

        return dlcs.entrySet().stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    public Map<String, Set<String>> getDlcTabCodeMap() {
        Map<String, Set<String>> map = new LinkedHashMap<>();
        tabs.forEach((key, value) -> map.putIfAbsent(value, new HashSet<>()));

        for (LocalSong song : songs) {
            Set<String> set = map.get(song.dlcTab());
            if (set == null) {
                throw new RuntimeException("tab does not exist: " + song.dlcTab());
            }

            set.add(song.dlcCode());
        }

        return map;
    }

    public Map<String, List<LocalSong>> getTabSongMap() {
        Map<String, List<LocalSong>> map = new LinkedHashMap<>();
        tabs.forEach((key, value) -> map.putIfAbsent(value, new ArrayList<>()));

        for (LocalSong song : songs) {
            List<LocalSong> list = map.get(song.dlcTab());
            if (list == null) {
                throw new RuntimeException("tab does not exist: " + song.dlcTab());
            }

            list.add(song);
        }

        map.forEach((key, value) -> {
            if (!DLC_COLLABORATION_TAB.equals(key)) {
                value.sort(new LocalSongComparator());
            } else {
                value.sort(new LocalSongComparator() {
                    @Serial
                    private static final long serialVersionUID = -217358984531426514L;

                    @Override
                    public int compare(LocalSong o1, LocalSong o2) {
                        int left = getDlcPriority(o1.dlcCode());
                        int right = getDlcPriority(o2.dlcCode());

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

    protected int getDlcPriority(String dlcCode) {
        Dlc dlc = dlcs.get(dlcCode);
        return (dlc != null) ? dlc.priority() : -1;
    }

    protected boolean isUnlocked(LocalSong localSong, Set<String> ownedDlcs) {
        Unlock unlock = unlocks.get(localSong.id());
        if (unlock == null) {
            return ownedDlcs.contains(localSong.dlcCode());
        }

        for (Set<String> condition : unlock.conditions) {
            if (ownedDlcs.containsAll(condition)) {
                return true;
            }
        }

        return false;
    }
}
