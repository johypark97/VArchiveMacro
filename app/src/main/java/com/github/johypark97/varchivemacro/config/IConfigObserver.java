package com.github.johypark97.varchivemacro.config;

public interface IConfigObserver {
    default void subscribe(IConfigObservable observable) {
        observable.register(this);
    }

    default void unsubscribe(IConfigObservable observable) {
        observable.unregister(this);
    }

    void configWillBeSaved(ConfigData configDataRef);

    void configIsLoaded(ConfigData configDataRef);
}
