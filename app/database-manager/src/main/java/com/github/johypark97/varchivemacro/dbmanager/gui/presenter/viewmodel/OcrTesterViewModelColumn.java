package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableColumnLookup;
import com.google.common.collect.BiMap;
import java.util.Map;

public interface OcrTesterViewModelColumn {
    enum ColumnKey {
        ACCURACY, DISTANCE, ID, MATCH, NORMALIZED_TITLE, SCANNED_TITLE, SONG_COMPOSER, SONG_DLC, SONG_DLC_TAB, SONG_TITLE
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

            builder.add(ColumnKey.MATCH);
            builder.add(ColumnKey.ID);
            builder.add(ColumnKey.DISTANCE);
            builder.add(ColumnKey.ACCURACY);
            builder.add(ColumnKey.SCANNED_TITLE);
            builder.add(ColumnKey.NORMALIZED_TITLE);
            builder.add(ColumnKey.SONG_TITLE);
            builder.add(ColumnKey.SONG_COMPOSER);
            builder.add(ColumnKey.SONG_DLC);
            builder.add(ColumnKey.SONG_DLC_TAB);

            return builder.build();
        }

        private Map<ColumnKey, String> createNameMap() {
            NameMapBuilder<ColumnKey> builder = new NameMapBuilder<>();

            builder.setEnumClass(ColumnKey.class);
            builder.setConverter((x) -> switch (x) {
                case ACCURACY -> "accuracy";
                case DISTANCE -> "distance";
                case ID -> "id";
                case MATCH -> "isMatch";
                case NORMALIZED_TITLE -> "normalizedTitle";
                case SCANNED_TITLE -> "scannedTitle";
                case SONG_COMPOSER -> "composer";
                case SONG_DLC -> "dlc";
                case SONG_DLC_TAB -> "dlcTab";
                case SONG_TITLE -> "title";
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
