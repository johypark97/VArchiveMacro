package com.github.johypark97.varchivemacro.macro.common.config.storage;

import java.io.IOException;

public interface ConfigStorage<T> {
    T read() throws IOException;

    void write(T config) throws IOException;
}
