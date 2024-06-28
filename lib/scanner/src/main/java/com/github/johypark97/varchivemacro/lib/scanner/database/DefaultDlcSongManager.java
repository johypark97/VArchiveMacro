package com.github.johypark97.varchivemacro.lib.scanner.database;

import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.DlcLocalSongComparator;
import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.LocalSongComparator;
import com.github.johypark97.varchivemacro.lib.scanner.database.datastruct.DlcData;
import com.github.johypark97.varchivemacro.lib.scanner.database.datastruct.SongData;
import com.github.johypark97.varchivemacro.lib.scanner.database.datastruct.TabData;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultDlcSongManager extends DefaultSongManager implements DlcSongManager {
    private final List<LocalDlcSong> dlcSongList;

    private final Map<Integer, LocalDlcSong> lookupId;
    private final Map<String, DlcData> dlcMap; // dlc code - dlc data
    private final Map<String, TabData> tabMap; // tab name - tab data

    protected DefaultDlcSongManager(List<SongData> songDataList, Map<String, DlcData> dlcMap,
            Map<String, TabData> tabMap) {
        super(songDataList);

        this.dlcMap = dlcMap;
        this.tabMap = tabMap;

        Map<String, String> codeTabMap = new HashMap<>(); // dlc code - tab name
        tabMap.forEach((tabName, tab) -> tab.dlcCode.forEach(
                (dlcCode) -> codeTabMap.put(dlcCode, tabName)));

        dlcSongList = getSongList().stream().map((x) -> {
            DlcData dlc = dlcMap.get(x.dlcCode);

            String dlcName = (dlc != null) ? dlc.name() : "";
            String dlcTab = codeTabMap.getOrDefault(x.dlcCode, "");
            int dlcPriority = (dlc != null) ? dlc.priority() : -1;

            return new LocalDlcSong(x, dlcName, dlcTab, dlcPriority);
        }).toList();

        lookupId = newLookupId(dlcSongList);
    }

    public static DefaultDlcSongManager load(Path songPath, Path dlcPath, Path tabPath)
            throws IOException {
        return new DefaultDlcSongManager(loadSongDataList(songPath), loadDlcMap(dlcPath),
                loadTabMap(tabPath));
    }

    protected static Map<String, DlcData> loadDlcMap(Path dlcPath) throws IOException {
        return DlcData.loadJson(dlcPath);
    }

    protected static Map<String, TabData> loadTabMap(Path tabPath) throws IOException {
        return TabData.loadJson(tabPath);
    }

    private static Map<Integer, LocalDlcSong> newLookupId(List<LocalDlcSong> songList) {
        Function<LocalDlcSong, Integer> keyMapper = (x) -> x.id;
        Function<LocalDlcSong, LocalDlcSong> valueMapper = (x) -> x;

        return songList.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @Override
    public LocalDlcSong getDlcSong(int id) {
        return lookupId.get(id);
    }

    @Override
    public List<LocalDlcSong> getDlcSongList() {
        return dlcSongList.stream().sorted(new LocalSongComparator()).toList();
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
        return tabMap.keySet().stream().toList();
    }

    @Override
    public Set<String> getDlcTabSet() {
        return tabMap.keySet();
    }

    @Override
    public Map<String, String> getDlcCodeNameMap() {
        Function<Entry<String, DlcData>, String> keyMapper = Entry::getKey;
        Function<Entry<String, DlcData>, String> valueMapper = (x) -> x.getValue().name();

        return dlcMap.entrySet().stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @Override
    public Map<String, Set<String>> getDlcTabCodeMap() {
        BinaryOperator<Set<String>> mergeFunction =
                (o1, o2) -> Stream.of(o1, o2).flatMap(Set::stream).collect(Collectors.toSet());

        return tabMap.entrySet().stream().collect(
                Collectors.toMap(Entry::getKey, (x) -> x.getValue().dlcCode, mergeFunction,
                        LinkedHashMap::new));
    }

    @Override
    public Map<String, List<LocalDlcSong>> getTabSongMap() {
        Map<String, List<LocalDlcSong>> map = new LinkedHashMap<>();

        tabMap.forEach((key, value) -> {
            List<LocalDlcSong> list =
                    dlcSongList.stream().filter((x) -> value.dlcCode.contains(x.dlcCode))
                            .sorted(new DlcLocalSongComparator()).toList();
            map.put(key, list);
        });

        return map;
    }
}
