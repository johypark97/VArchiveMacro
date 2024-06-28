package com.github.johypark97.varchivemacro.lib.scanner.database;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.api.Api;
import com.github.johypark97.varchivemacro.lib.scanner.api.Api.Board;
import com.github.johypark97.varchivemacro.lib.scanner.api.ApiException;
import com.github.johypark97.varchivemacro.lib.scanner.api.RecordFetcher;
import com.github.johypark97.varchivemacro.lib.scanner.database.datastruct.RecordData;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultRecordManager implements RecordManager {
    private static final List<Board> BOARDS = Arrays.stream(Board.values())
            .filter((x) -> !EnumSet.of(Board.SC5, Board.SC10, Board.SC15).contains(x)).toList();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final RecordMap recordMap;

    protected DefaultRecordManager(RecordMap recordMap) {
        this.recordMap = recordMap;
    }

    public static DefaultRecordManager loadLocal(Path path) throws IOException {
        RecordMap recordMap = new RecordMap();

        List<RecordData> recordDataList = RecordData.loadJson(path);
        recordDataList.stream().map(RecordData::toLocalRecord).forEach(recordMap::add);

        return new DefaultRecordManager(recordMap);
    }

    public static DefaultRecordManager loadRemote(String djName)
            throws GeneralSecurityException, IOException, InterruptedException, ApiException {
        RecordMap recordMap = new RecordMap();

        RecordFetcher fetcher = Api.newRecordFetcher(djName);
        for (Button button : Button.values()) {
            for (Board board : BOARDS) {
                fetcher.fetch(button, board);

                Button b = fetcher.getResult().button;
                for (RecordFetcher.FloorJson floor : fetcher.getResult().floors) {
                    for (RecordFetcher.PatternJson pattern : floor.patterns) {
                        int id = pattern.id;
                        Pattern p = pattern.pattern;
                        float rate = pattern.rate;
                        boolean maxCombo = pattern.maxCombo == 1;

                        recordMap.add(new LocalRecord(id, b, p, rate, maxCombo));
                    }
                }
            }
        }

        return new DefaultRecordManager(recordMap);
    }

    public void saveJson(Path path) throws IOException {
        List<RecordData> recordList = new LinkedList<>();

        lock.readLock().lock();
        try {
            recordMap.forEach((id, buttonMap) -> buttonMap.forEach(
                    (button, patternMap) -> patternMap.values().stream()
                            .map(RecordData::fromLocalRecord).forEach(recordList::add)));
        } finally {
            lock.readLock().unlock();
        }

        RecordData.saveJson(path, recordList);
    }

    @Override
    public boolean updateRecord(LocalRecord record) {
        lock.writeLock().lock();
        try {
            return recordMap.add(record);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public LocalRecord findSameRecord(LocalRecord record) {
        return getRecord(record.id, record.button, record.pattern);
    }

    @Override
    public LocalRecord getRecord(int id, Button button, Pattern pattern) {
        lock.readLock().lock();
        try {
            return recordMap.find(id, button, pattern);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Map<Pattern, LocalRecord> getRecord(int id, Button button) {
        Map<Pattern, LocalRecord> map = new EnumMap<>(Pattern.class);

        lock.readLock().lock();
        ButtonMap buttonMap = recordMap.get(id);
        if (buttonMap != null) {
            PatternMap patternMap = buttonMap.get(button);
            if (patternMap != null) {
                map.putAll(patternMap);
            }
        }
        lock.readLock().unlock();

        return map;
    }

    @Override
    public Map<Button, Map<Pattern, LocalRecord>> getRecord(int id) {
        Map<Button, Map<Pattern, LocalRecord>> map = new EnumMap<>(Button.class);

        lock.readLock().lock();
        ButtonMap buttonMap = recordMap.get(id);
        if (buttonMap != null) {
            buttonMap.forEach((button, patternMap) -> map.computeIfAbsent(button,
                    (x) -> new EnumMap<>(Pattern.class)).putAll(patternMap));
        }
        lock.readLock().unlock();

        return map;
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


    protected static class ButtonMap extends EnumMap<Button, PatternMap> {
        @Serial
        private static final long serialVersionUID = -7755798461331119049L;

        public ButtonMap() {
            super(Button.class);
        }

        public boolean add(LocalRecord record) {
            return computeIfAbsent(record.button, (x) -> new PatternMap()).add(record);
        }

        public LocalRecord find(Button button, Pattern pattern) {
            PatternMap map = get(button);
            return (map != null) ? map.find(pattern) : null;
        }
    }


    protected static class PatternMap extends EnumMap<Pattern, LocalRecord> {
        @Serial
        private static final long serialVersionUID = -6011022680688092958L;

        public PatternMap() {
            super(Pattern.class);
        }

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
