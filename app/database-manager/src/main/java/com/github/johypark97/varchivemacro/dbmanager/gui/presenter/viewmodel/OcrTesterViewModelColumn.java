package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableColumnLookup;
import com.google.common.collect.BiMap;
import java.util.Map;

public interface OcrTesterViewModelColumn {
    enum ColumnKey {
        // @formatter:off
        ACCURACY,
        DISTANCE,
        NORM_SCANNED_TITLE,
        NOTE,
        PASS,
        RECOGNIZED_SONG_COMPOSER,
        RECOGNIZED_SONG_ID,
        RECOGNIZED_SONG_TITLE,
        TEST_SONG_COMPOSER,
        TEST_SONG_DLC,
        TEST_SONG_DLC_TAB,
        TEST_SONG_ID,
        TEST_SONG_NORM_TITLE,
        TEST_SONG_TITLE,
        // @formatter:on
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

            builder.add(ColumnKey.TEST_SONG_ID);
            builder.add(ColumnKey.TEST_SONG_TITLE);
            builder.add(ColumnKey.TEST_SONG_COMPOSER);
            builder.add(ColumnKey.TEST_SONG_DLC);
            builder.add(ColumnKey.TEST_SONG_DLC_TAB);
            builder.add(ColumnKey.TEST_SONG_NORM_TITLE);
            builder.add(ColumnKey.NORM_SCANNED_TITLE);
            builder.add(ColumnKey.RECOGNIZED_SONG_ID);
            builder.add(ColumnKey.RECOGNIZED_SONG_TITLE);
            builder.add(ColumnKey.RECOGNIZED_SONG_COMPOSER);
            builder.add(ColumnKey.ACCURACY);
            builder.add(ColumnKey.DISTANCE);
            builder.add(ColumnKey.NOTE);
            builder.add(ColumnKey.PASS);

            return builder.build();
        }

        private Map<ColumnKey, String> createNameMap() {
            NameMapBuilder<ColumnKey> builder = new NameMapBuilder<>();

            builder.setEnumClass(ColumnKey.class);
            builder.setConverter((x) -> switch (x) {
                case ACCURACY -> "accuracy";
                case DISTANCE -> "distance";
                case NORM_SCANNED_TITLE -> "normScannedTitle";
                case NOTE -> "note";
                case PASS -> "test pass";
                case RECOGNIZED_SONG_COMPOSER -> "r.composer";
                case RECOGNIZED_SONG_ID -> "r.id";
                case RECOGNIZED_SONG_TITLE -> "r.title";
                case TEST_SONG_COMPOSER -> "t.composer";
                case TEST_SONG_DLC -> "t.dlc";
                case TEST_SONG_DLC_TAB -> "t.dlcTab";
                case TEST_SONG_ID -> "t.id";
                case TEST_SONG_NORM_TITLE -> "t.normTitle";
                case TEST_SONG_TITLE -> "t.title";
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
