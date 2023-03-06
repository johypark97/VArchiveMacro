package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Board;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Pattern;
import com.github.johypark97.varchivemacro.lib.common.api.RecordFetcher;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecordManager {
    private static final List<Board> BOARDS = Arrays.stream(Board.values())
            .filter((x) -> !EnumSet.of(Board.SC5, Board.SC10, Board.SC15).contains(x)).toList();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private RecordMap managedRecords = new RecordMap();

    public LocalRecord getRecord(int id, Button button, Pattern pattern) {
        lock.readLock().lock();
        try {
            return managedRecords.find(id, button, pattern);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Float> getRecords(int id) {
        List<Float> records = Stream.generate(() -> -1f).limit(16)
                .collect(Collectors.toCollection(ArrayList::new));

        lock.readLock().lock();
        ButtonMap buttonMap = managedRecords.get(id);
        if (buttonMap != null) {
            buttonMap.forEach((button, patternMap) -> patternMap.forEach((pattern, record) -> {
                int b = switch (button) {
                    case _4 -> 0;
                    case _5 -> 1;
                    case _6 -> 2;
                    case _8 -> 3;
                };
                int p = switch (pattern) {
                    case NM -> 0;
                    case HD -> 1;
                    case MX -> 2;
                    case SC -> 3;
                };

                records.set(p + b * 4, record.score);
            }));
        }
        lock.readLock().unlock();

        return records;
    }

    public void loadJson(Path path) throws IOException {
        RecordMap map = new RecordMap();

        List<LocalRecord> recordList = LocalRecord.loadJson(path);
        recordList.forEach(map::add);

        lock.writeLock().lock();
        managedRecords = map;
        lock.writeLock().unlock();
    }

    // TODO: Add async version
    public void loadRemote(String djName)
            throws GeneralSecurityException, IOException, InterruptedException {
        RecordMap map = new RecordMap();

        RecordFetcher fetcher = Api.newRecordFetcher(djName);
        for (Button button : Button.values()) {
            for (Board board : BOARDS) {
                fetcher.fetch(button, board);

                Button b = fetcher.getResult().button;
                for (RecordFetcher.FloorJson floor : fetcher.getResult().floors) {
                    for (RecordFetcher.PatternJson pattern : floor.patterns) {
                        int id = pattern.id;
                        Pattern p = pattern.pattern;
                        float score = pattern.score;
                        int maxCombo = pattern.maxCombo;

                        map.add(new LocalRecord(id, b, p, score, maxCombo));
                    }
                }
            }
        }

        lock.writeLock().lock();
        managedRecords = map;
        lock.writeLock().unlock();
    }

    public void saveJson(Path path) throws IOException {
        List<LocalRecord> recordList = new LinkedList<>();

        lock.readLock().lock();
        try {
            managedRecords.forEach((id, buttonMap) -> buttonMap.forEach(
                    (button, patternMap) -> recordList.addAll(patternMap.values())));
        } finally {
            lock.readLock().unlock();
        }

        LocalRecord.saveJson(path, recordList);
    }

    protected static class RecordMap extends HashMap<Integer, ButtonMap> {
        @Serial
        private static final long serialVersionUID = 5499528273876284886L;

        public boolean add(LocalRecord record) {
            return computeIfAbsent(record.id, (x) -> new ButtonMap()).add(record);
        }

        public LocalRecord find(int id, Button button, Pattern pattern) {
            ButtonMap map = get(id);
            return (map != null) ? map.find(button, pattern) : null;
        }
    }


    protected static class ButtonMap extends HashMap<Button, PatternMap> {
        @Serial
        private static final long serialVersionUID = -7755798461331119049L;

        public boolean add(LocalRecord record) {
            return computeIfAbsent(record.button, (x) -> new PatternMap()).add(record);
        }

        public LocalRecord find(Button button, Pattern pattern) {
            PatternMap map = get(button);
            return (map != null) ? map.find(pattern) : null;
        }
    }


    protected static class PatternMap extends HashMap<Pattern, LocalRecord> {
        @Serial
        private static final long serialVersionUID = -6011022680688092958L;

        public boolean add(LocalRecord newRecord) {
            LocalRecord storedRecord = get(newRecord.pattern);
            if (storedRecord == null) {
                put(newRecord.pattern, newRecord);
                return true;
            } else {
                return storedRecord.update(newRecord);
            }
        }

        public LocalRecord find(Pattern pattern) {
            return get(pattern);
        }
    }
}
