package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.ISongRecordManager;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SongRecordDataModel {
    interface ISongRecordModel {
        List<String> getDlcTabList();

        Map<String, List<LocalSong>> getTabSongMap();

        Map<String, List<LocalSong>> getTabSongMap(Set<String> selectedTabs);

        Table<Button, Pattern, String> getRecordTable(int id);
    }


    class SongRecordModel implements ISongRecordModel {
        private final ISongRecordManager songRecordManager;

        public SongRecordModel(ISongRecordManager songRecordManager) {
            this.songRecordManager = songRecordManager;
        }

        @Override
        public List<String> getDlcTabList() {
            return songRecordManager.getDlcTabList();
        }

        @Override
        public Map<String, List<LocalSong>> getTabSongMap() {
            return songRecordManager.getTabSongMap();
        }

        @Override
        public Map<String, List<LocalSong>> getTabSongMap(Set<String> selectedTabs) {
            return songRecordManager.getTabSongMap(selectedTabs);
        }

        @Override
        public Table<Button, Pattern, String> getRecordTable(int id) {
            Table<Button, Pattern, String> table = HashBasedTable.create();

            songRecordManager.getRecordMap(id).forEach((button, patternMap) -> patternMap.forEach(
                    (pattern, value) -> table.put(Button.valueOf(button), Pattern.valueOf(pattern),
                            value)));

            return table;
        }
    }
}
