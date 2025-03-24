package com.github.johypark97.varchivemacro.lib.common.manager;

import java.util.function.Supplier;

public interface InstanceManager<T> {
    <U extends T> void setConstructor(Class<U> cls, Supplier<U> constructor);

    <U extends T> U getInstance(Class<U> cls);
}
