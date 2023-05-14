package com.github.johypark97.varchivemacro.macro.core.protocol;

public interface SyncChannel {
    /**
     * @param <T> Request
     * @param <U> Response
     */
    interface Requester<T, U> {
        U request(T r);
    }


    /**
     * @param <T> Event
     * @param <U> Request
     * @param <V> Response
     */
    interface Server<T, U, V> {
        void addClient(Client<T, U, V> client);

        void notify(T e);
    }


    /**
     * @param <T> Event
     * @param <U> Request
     * @param <V> Response
     */
    interface Client<T, U, V> {
        void onAddClient(Requester<U, V> requester);

        void onNotify(T e);
    }
}
