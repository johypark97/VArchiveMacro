package com.github.johypark97.varchivemacro.macro.fxgui.model;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface RecordModel {
    boolean loadLocal() throws IOException;

    void loadRemote(String djName, Consumer<Boolean> onDone, BiConsumer<String, Exception> onThrow);
}
