package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct;

import java.nio.file.Path;

public class GroundTruthGeneratorConfig {
    public Path cacheDir = Path.of("cache");
    public Path groundTruthDir = Path.of("ground-truth");
    public Path preparedDir = Path.of("prepared");
}
