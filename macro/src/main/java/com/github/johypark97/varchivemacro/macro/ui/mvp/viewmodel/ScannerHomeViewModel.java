package com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;

public class ScannerHomeViewModel {
    public record SongTreeViewData(String category, String title, String composer, int songId) {
        public static SongTreeViewData from(Song.Pack.Category category) {
            return new SongTreeViewData(category.name(), null, null, -1);
        }

        public static SongTreeViewData from(Song song) {
            return new SongTreeViewData(null, song.title(), song.composer(), song.songId());
        }
    }


    public static class SongRecord {
        public final boolean[][] maxCombo = new boolean[4][4];
        public final float[][] rate = new float[4][4];

        public SongRecord() {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    rate[i][i] = -1;
                }
            }
        }

        public static SongRecord from(SongRecordTable table) {
            SongRecord record = new SongRecord();

            table.recordStream().forEach(cell -> {
                int row = cell.button().getWeight();
                int column = cell.pattern().getWeight();

                record.maxCombo[row][column] = cell.record().maxCombo();
                record.rate[row][column] = cell.record().rate();
            });

            return record;
        }
    }
}
