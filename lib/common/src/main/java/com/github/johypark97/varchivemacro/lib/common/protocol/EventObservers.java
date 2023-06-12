package com.github.johypark97.varchivemacro.lib.common.protocol;

public interface EventObservers {
    interface EventObservable<T> {
        void addEventObserver(EventObserver<T> observer);

        void addEventObserver(EventObserver<T> observer, T event);

        void deleteEventObservers();

        void deleteEventObservers(EventObserver<T> observer);

        void deleteEventObservers(EventObserver<T> observer, T event);

        void notifyEventObservers(T event);
    }


    interface EventObserver<T> {
        void onNotifyEventObservers(T event);
    }
}
