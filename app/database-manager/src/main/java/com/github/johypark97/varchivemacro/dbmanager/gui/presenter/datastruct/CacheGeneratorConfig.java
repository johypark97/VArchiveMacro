package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct;

import java.nio.file.Path;

public class CacheGeneratorConfig {
    public Path cacheDir = Path.of("cache");
    public int captureDelay = 50;
    public int inputDuration = 50;
}
