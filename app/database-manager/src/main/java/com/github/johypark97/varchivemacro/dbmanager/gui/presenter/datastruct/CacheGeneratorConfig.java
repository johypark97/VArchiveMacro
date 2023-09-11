package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct;

import java.nio.file.Path;

public class CacheGeneratorConfig {
    public Path cacheDir = Path.of("cache");
    public int captureDelay = 100;
    public int continuousCaptureDelay = 50;
    public int count = 1;
    public int inputDuration = 50;
}
