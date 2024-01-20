package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModelHelper {
    public static BiConsumer<Void, Throwable> defaultWhenComplete(Runnable onDone,
            Runnable onCancel, Consumer<Throwable> onThrow) {
        return (unused, throwable) -> {
            if (throwable == null) {
                onDone.run();
                return;
            }

            Throwable pThrowable = throwable;
            do {
                if (pThrowable instanceof InterruptedException) {
                    onCancel.run();
                    return;
                }

                pThrowable = pThrowable.getCause();
            } while (pThrowable != null);

            onThrow.accept(throwable);
        };
    }
}
