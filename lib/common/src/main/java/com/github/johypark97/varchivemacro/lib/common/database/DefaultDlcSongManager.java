package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.comparator.LocalSongComparator;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.Dlc;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.Tab;
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

public class DefaultDlcSongManager extends DefaultSongManager implements DlcSongManager {
    private static final int DLC_COUNT_CONDITION = 1;

    private final Map<Integer, String> tabMap;
    private final Map<String, Dlc> dlcMap;

    public DefaultDlcSongManager(Path songPath, Path dlcPath, Path tabPath) throws IOException {
        super(songPath);

        dlcMap = Dlc.loadJson(dlcPath);
        tabMap = Tab.loadJson(tabPath);
    }

    private int getDlcPriority(String dlcCode) {
        Dlc dlc = dlcMap.get(dlcCode);
        return (dlc != null) ? dlc.priority() : -1;
    }

    @Override
    public List<String> getDlcCodeList() {
        return dlcMap.keySet().stream().toList();
    }

    @Override
    public Set<String> getDlcCodeSet() {
        return dlcMap.keySet();
    }

    @Override
    public List<String> getDlcTabList() {
        return tabMap.values().stream().toList();
    }

    @Override
    public Set<String> getDlcTabSet() {
        return new HashSet<>(tabMap.values());
    }

    @Override
    public Map<String, String> getDlcCodeNameMap() {
        Function<Entry<String, Dlc>, String> keyMapper = Entry::getKey;
        Function<Entry<String, Dlc>, String> valueMapper = (x) -> x.getValue().name();

        return dlcMap.entrySet().stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @Override
    public Map<String, Set<String>> getDlcTabCodeMap() {
        Map<String, Set<String>> map = new LinkedHashMap<>();
        tabMap.forEach((key, value) -> map.putIfAbsent(value, new HashSet<>()));

        for (LocalSong song : getSongList()) {
            Set<String> set = map.get(song.dlcTab());
            if (set == null) {
                throw new RuntimeException("tab does not exist: " + song.dlcTab());
            }

            set.add(song.dlcCode());
        }

        return map;
    }

    @Override
    public Map<String, List<LocalSong>> getTabSongMap() {
        Map<String, List<LocalSong>> map = new LinkedHashMap<>();
        tabMap.forEach((key, value) -> map.putIfAbsent(value, new ArrayList<>()));

        for (LocalSong song : getSongList()) {
            List<LocalSong> list = map.get(song.dlcTab());
            if (list == null) {
                throw new RuntimeException("tab does not exist: " + song.dlcTab());
            }

            list.add(song);
        }

        map.forEach((key, value) -> {
            int count = value.stream().map(LocalSong::dlc).collect(Collectors.toSet()).size();
            if (count == DLC_COUNT_CONDITION) {
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
}
