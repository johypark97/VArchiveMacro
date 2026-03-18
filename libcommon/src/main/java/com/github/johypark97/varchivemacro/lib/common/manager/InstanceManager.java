package com.github.johypark97.varchivemacro.lib.common.manager;

import java.util.function.Supplier;

public interface InstanceManager<T> {
    <U extends T> void setConstructor(Class<U> cls, Supplier<U> constructor);

    <U extends T> U getInstance(Class<U> cls);

    <U extends T> U setInstance(Class<U> cls, U instance);

    <U extends T> U removeInstance(Class<U> cls);
}
