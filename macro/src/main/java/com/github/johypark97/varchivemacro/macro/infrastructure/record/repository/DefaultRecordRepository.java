package com.github.johypark97.varchivemacro.macro.infrastructure.record.repository;

import com.github.johypark97.varchivemacro.lib.scanner.api.ApiException;
import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRecordRepository implements RecordRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRecordRepository.class);

    private static final Path RECORD_PATH = Path.of("records.json");

    private DefaultRecordManager recordManager;

    @Override
    public void save() throws IOException {
        recordManager.saveJson(RECORD_PATH);
    }

    @Override
    public boolean loadLocal() throws IOException {
        if (!Files.exists(RECORD_PATH)) {
            return false;
        }

        recordManager = new DefaultRecordManager(RECORD_PATH);

        return true;
    }

    @Override
    public void loadRemote(String djName, Consumer<Boolean> onDone,
            BiConsumer<String, Exception> onThrow) {
        CompletableFuture.supplyAsync(() -> {
            Language language = Language.getInstance();

            try {
                recordManager = new DefaultRecordManager(djName);
            } catch (ApiException e) {
                LOGGER.atError().setCause(e).log("ApiException");
                Platform.runLater(() -> {
                    String message = language.getString("recordRepository.apiException");
                    onThrow.accept(message, e);
                });
                return false;
            } catch (GeneralSecurityException e) {
                LOGGER.atError().setCause(e).log("GeneralSecurityException");
                Platform.runLater(() -> {
                    String message =
                            language.getString("recordRepository.generalSecurityException");
                    onThrow.accept(message, e);
                });
                return false;
            } catch (IOException e) {
                LOGGER.atError().setCause(e).log("Network IOException");
                Platform.runLater(() -> {
                    String message = language.getString("recordRepository.networkIOException");
                    onThrow.accept(message, e);
                });
                return false;
            } catch (InterruptedException e) {
                return false;
            }

            try {
                recordManager.saveJson(RECORD_PATH);
            } catch (IOException e) {
                LOGGER.atError().setCause(e).log("File IOException");
                Platform.runLater(() -> {
                    String message = language.getString("recordRepository.fileIOException");
                    onThrow.accept(message, e);
                });
                return false;
            }

            return true;
        }).exceptionally(x -> {
            Platform.runLater(() -> {
                throw new RuntimeException(x);
            });

            return false;
        }).thenAccept(x -> Platform.runLater(() -> onDone.accept(x)));
    }

    @Override
    public List<LocalRecord> getRecordList(int id) {
        List<LocalRecord> list = new ArrayList<>(16);

        recordManager.getRecord(id).values().forEach(x -> list.addAll(x.values()));

        return list;
    }

    @Override
    public LocalRecord findSameRecord(LocalRecord record) {
        return recordManager.findSameRecord(record);
    }

    @Override
    public void updateRecord(LocalRecord record) {
        recordManager.updateRecord(record);
    }
}
