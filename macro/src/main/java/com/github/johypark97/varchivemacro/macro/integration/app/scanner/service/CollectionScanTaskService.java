package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import java.io.IOException;
import java.util.Set;
import javafx.concurrent.Task;

public interface CollectionScanTaskService {
    Task<Void> createTask(Set<String> selectedCategorySet) throws IOException;

    boolean stopTask();
}
