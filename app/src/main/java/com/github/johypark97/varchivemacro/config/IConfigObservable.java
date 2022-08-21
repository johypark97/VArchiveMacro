package com.github.johypark97.varchivemacro.config;

public interface IConfigObservable {
    enum NotifyType {
        IS_LOADED, WILL_BE_SAVED
    }

    void register(IConfigObserver observer);

    void unregister(IConfigObserver observer);

    void notifyObservers(NotifyType type);
}
