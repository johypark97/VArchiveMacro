package com.github.johypark97.varchivemacro.lib.common.protocol;

public interface Observers {
    interface Observable<T> {
        void addObserver(Observer<T> observer);

        void deleteObservers();

        void deleteObservers(Observer<T> observer);

        void notifyObservers(T argument);
    }


    interface Observer<T> {
        void onNotifyObservers(T argument);
    }
}
