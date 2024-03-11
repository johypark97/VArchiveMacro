package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service;

import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public abstract class BaseService<T extends Task<U>, U> extends Service<U> {
    private Supplier<T> taskConstructor;

    public void setTaskConstructor(Supplier<T> constructor) {
        taskConstructor = constructor;
    }

    protected T newTask() {
        return taskConstructor.get();
    }

    @Override
    protected Task<U> createTask() {
        return newTask();
    }

    public static abstract class Builder<B extends Builder<B, S>, S extends BaseService<?, ?>> {
        private final Class<S> cls;

        private Consumer<Throwable> onThrow;
        private Runnable onCancel;
        private Runnable onDone;

        protected Builder(Class<S> cls) {
            this.cls = cls;
        }

        public B setOnCancel(Runnable value) {
            onCancel = value;
            return getInstance();
        }

        public B setOnDone(Runnable value) {
            onDone = value;
            return getInstance();
        }

        public B setOnThrow(Consumer<Throwable> value) {
            onThrow = value;
            return getInstance();
        }

        public void build() {
            Objects.requireNonNull(onCancel);
            Objects.requireNonNull(onDone);
            Objects.requireNonNull(onThrow);

            S service = ServiceManager.getInstance().create(cls);
            if (service == null) {
                throw new RuntimeException("Service has already been created: " + cls);
            }

            service.setOnCancelled(event -> onCancel.run());
            service.setOnFailed(event -> onThrow.accept(service.getException()));
            service.setOnSucceeded(event -> onDone.run());
        }

        protected abstract B getInstance();
    }
}
