package com.github.johypark97.varchivemacro.macro.core.backend;

import com.github.johypark97.varchivemacro.macro.core.clientmacro.AnalyzeKey;
import com.github.johypark97.varchivemacro.macro.core.clientmacro.Direction;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface IBackend {
    boolean loadSongs() throws IOException;

    boolean loadLocalRecord() throws IOException;

    boolean isCommandRunning();

    void stopCommand();

    void loadRemoteRecord(String djName);

    void runClientMacro(AnalyzeKey analyzeKey, Direction direction, int captureDelay,
            int captureDuration, int count, int keyInputDuration);

    void startScan(Path cacheDir, int captureDelay, int inputDuration, Set<String> ownedDlcTabs);

    void startAnalyze();

    void collectResult();

    void uploadRecord(Path accountPath, int uploadDelay);
}
