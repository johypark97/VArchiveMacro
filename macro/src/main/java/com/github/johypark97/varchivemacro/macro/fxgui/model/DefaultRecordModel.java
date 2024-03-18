package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.api.ApiException;
import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
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

public class DefaultRecordModel implements RecordModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRecordModel.class);

    private static final Path RECORD_PATH = Path.of("records.json");

    private DefaultRecordManager recordManager;

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
            try {
                recordManager = new DefaultRecordManager(djName);
            } catch (ApiException e) {
                LOGGER.atError().log("ApiException", e);
                Platform.runLater(
                        () -> onThrow.accept("Failed to load records from the server.", e));
                return false;
            } catch (GeneralSecurityException e) {
                LOGGER.atError().log("GeneralSecurityException", e);
                Platform.runLater(
                        () -> onThrow.accept("Failed to establish a secure connection.", e));
                return false;
            } catch (IOException e) {
                LOGGER.atError().log("IOException", e);
                Platform.runLater(() -> onThrow.accept("Network IO error", e));
                return false;
            } catch (InterruptedException e) {
                return false;
            }

            try {
                recordManager.saveJson(RECORD_PATH);
            } catch (IOException e) {
                LOGGER.atError().log("IOException", e);
                Platform.runLater(() -> onThrow.accept("File IO error", e));
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
}
