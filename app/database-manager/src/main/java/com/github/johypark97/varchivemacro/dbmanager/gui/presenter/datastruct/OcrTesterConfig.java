package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct;

import java.nio.file.Path;

public class OcrTesterConfig {
    public Path cachePath = Path.of("cache");
    public Path trainedDataDirectory = Path.of("");
    public String trainedDataLanguage = "";
}
