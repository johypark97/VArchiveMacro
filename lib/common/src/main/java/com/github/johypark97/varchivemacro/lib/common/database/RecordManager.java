package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.Board;
import com.github.johypark97.varchivemacro.lib.common.api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Pattern;
import com.github.johypark97.varchivemacro.lib.common.api.RecordFetcher;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RecordManager {
    private static final List<Board> BOARDS =
            List.of(Board._1, Board._2, Board._3, Board._4, Board._5, Board._6, Board._7, Board._8,
                    Board._9, Board._10, Board._11, Board.MX, Board.SC);
    private static final List<Button> BUTTONS = List.of(Button._4, Button._5, Button._6, Button._8);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private RecordMap managedRecords = new RecordMap();

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
        for (Button button : BUTTONS) {
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
}


class RecordMap extends HashMap<Integer, ButtonMap> {
    @Serial
    private static final long serialVersionUID = 5499528273876284886L;

    public boolean add(LocalRecord record) {
        return computeIfAbsent(record.id, (x) -> new ButtonMap()).add(record);
    }
}


class ButtonMap extends HashMap<Button, PatternMap> {
    @Serial
    private static final long serialVersionUID = -7755798461331119049L;

    public boolean add(LocalRecord record) {
        return computeIfAbsent(record.button, (x) -> new PatternMap()).add(record);
    }
}


class PatternMap extends HashMap<Pattern, LocalRecord> {
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
}
