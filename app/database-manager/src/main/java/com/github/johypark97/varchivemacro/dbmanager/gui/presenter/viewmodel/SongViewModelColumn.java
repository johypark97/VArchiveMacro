package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableColumnLookup;
import com.google.common.collect.BiMap;
import java.util.Map;

public interface SongViewModelColumn {
    enum ColumnKey {
        COMPOSER, DLC, DLC_CODE, DLC_TAB, ID, NUMBER, PRIORITY, REMOTE_TITLE, TITLE
    }


    class ColumnLookup implements TableColumnLookup<ColumnKey> {
        private final BiMap<ColumnKey, Integer> indexMap;
        private final Map<ColumnKey, String> nameMap;

        public ColumnLookup() {
            indexMap = createIndexMap();
            nameMap = createNameMap();
        }

        private BiMap<ColumnKey, Integer> createIndexMap() {
            IndexMapBuilder<ColumnKey> builder = new IndexMapBuilder<>();

            builder.add(ColumnKey.NUMBER);
            builder.add(ColumnKey.ID);
            builder.add(ColumnKey.TITLE);
            builder.add(ColumnKey.REMOTE_TITLE);
            builder.add(ColumnKey.COMPOSER);
            builder.add(ColumnKey.DLC);
            builder.add(ColumnKey.DLC_CODE);
            builder.add(ColumnKey.DLC_TAB);
            builder.add(ColumnKey.PRIORITY);

            return builder.build();
        }

        private Map<ColumnKey, String> createNameMap() {
            NameMapBuilder<ColumnKey> builder = new NameMapBuilder<>();

            builder.setEnumClass(ColumnKey.class);
            builder.setConverter((x) -> switch (x) {
                case COMPOSER -> "composer";
                case DLC -> "dlc";
                case DLC_CODE -> "dlcCode";
                case DLC_TAB -> "dlcTab";
                case ID -> "id";
                case NUMBER -> "number";
                case PRIORITY -> "priority";
                case REMOTE_TITLE -> "remoteTitle";
                case TITLE -> "title";
            });

            return builder.build();
        }

        @Override
        public int getCount() {
            return ColumnKey.values().length;
        }

        @Override
        public ColumnKey getKey(int index) {
            return indexMap.inverse().get(index);
        }

        @Override
        public int getIndex(ColumnKey key) {
            return indexMap.get(key);
        }

        @Override
        public String getName(ColumnKey key) {
            return nameMap.get(key);
        }
    }
}
