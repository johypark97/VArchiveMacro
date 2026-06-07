package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.storage;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.model.ButtonJson;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.model.PatternJson;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.model.RecordJson;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonRecordStorage implements RecordStorage {
    private final Gson gson;
    private final Path recordFilePath;

    public JsonRecordStorage(Gson gson, Path recordFilePath) {
        this.gson = gson;
        this.recordFilePath = recordFilePath;
    }

    @Override
    public List<SongRecordTable> load() throws IOException {
        List<RecordJson> recordJsonList;
        try (BufferedReader reader = Files.newBufferedReader(recordFilePath)) {
            recordJsonList = gson.fromJson(reader, new RecordJson.GsonListTypeToken());
        }

        Map<Integer, SongRecordTable> recordTableMap = new HashMap<>();

        recordJsonList.forEach(record -> {
            SongRecordTable table =
                    recordTableMap.computeIfAbsent(record.id(), SongRecordTable::new);

            table.setSongRecord(
                    record.button().toDomain(),
                    record.pattern().toDomain(),
                    new SongRecord(record.rate(), record.maxCombo()));
        });

        return recordTableMap.values().stream().toList();
    }

    @Override
    public void save(List<SongRecordTable> recordTableList) throws IOException {
        List<RecordJson> recordJsonList = new LinkedList<>();

        recordTableList.forEach(table -> table.recordStream().forEach(cell -> {
            ButtonJson button = ButtonJson.fromDomain(cell.button());
            PatternJson pattern = PatternJson.fromDomain(cell.pattern());

            recordJsonList.add(new RecordJson(
                    table.songId,
                    button,
                    pattern,
                    cell.record().rate(),
                    cell.record().maxCombo()));
        }));

        Files.writeString(recordFilePath, gson.toJson(recordJsonList));
    }
}
