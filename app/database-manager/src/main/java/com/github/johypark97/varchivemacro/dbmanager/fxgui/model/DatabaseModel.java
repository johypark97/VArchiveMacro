package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import java.io.IOException;
import java.nio.file.Path;

public interface DatabaseModel {
    void load(Path path) throws IOException;
}
