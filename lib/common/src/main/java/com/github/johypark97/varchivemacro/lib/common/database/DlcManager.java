package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.Dlc;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.Unlock;
import com.github.johypark97.varchivemacro.lib.common.database.util.LocalSongComparator;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DlcManager extends SongManager {
    private static final String DLC_COLLABORATION_TAB = "COLLABORATION";

    protected final List<Dlc> dlcs;
    protected final Map<Integer, Unlock> unlocks;

    protected final Map<String, Integer> lookupPriority;

    public DlcManager(Path songPath, Path dlcPath, Path unlockPath) throws IOException {
        super(songPath);

        dlcs = Dlc.loadJson(dlcPath);
        unlocks = Unlock.loadJson(unlockPath);

        lookupPriority = newLookupPriority(dlcs);
    }

    public Map<String, String> getDlcCodeNameMap() {
        Map<String, String> map = new LinkedHashMap<>();
        dlcs.forEach((x) -> map.put(x.code(), x.name()));
        return map;
    }

    public Map<String, List<LocalSong>> getTabSongMap() {
        Map<String, List<LocalSong>> map = new LinkedHashMap<>();
        dlcs.forEach((dlc) -> {
            String tab = dlc.tab();
            String code = dlc.code();

            List<LocalSong> list = map.computeIfAbsent(tab, x -> new ArrayList<>());
            songs.stream().filter((song) -> song.dlcCode().equals(code)).forEach(list::add);
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
                        int left = lookupPriority.getOrDefault(o1.dlcCode(), -1);
                        int right = lookupPriority.getOrDefault(o2.dlcCode(), -1);

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

    private Map<String, Integer> newLookupPriority(List<Dlc> dlcs) {
        Function<Dlc, String> keyMapper = Dlc::code;
        Function<Dlc, Integer> valueMapper = Dlc::priority;

        return dlcs.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }
}
