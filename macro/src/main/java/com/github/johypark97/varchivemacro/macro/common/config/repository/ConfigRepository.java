package com.github.johypark97.varchivemacro.macro.common.config.repository;

import java.io.IOException;
import java.util.function.UnaryOperator;

public interface ConfigRepository<T> {
    T find();

    void save(T config);

    void update(UnaryOperator<T> updateFunction);

    void refresh() throws IOException;

    void flush() throws IOException;
}
