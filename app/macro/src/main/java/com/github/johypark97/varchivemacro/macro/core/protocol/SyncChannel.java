package com.github.johypark97.varchivemacro.macro.core.protocol;

public interface SyncChannel {
    /**
     * @param <T> A data type that the server uses to send notifications to all clients
     * @param <U> A channel type that the client uses to contact the server
     */
    interface Server<T, U> {
        void addClient(Client<T, U> client);

        void notifyClients(T data);
    }


    /**
     * @param <T> A data type that the server uses to send notifications to all clients
     * @param <U> A channel type that the client uses to contact the server
     */
    interface Client<T, U> {
        void onAddClient(U channel);

        void onNotify(T data);
    }
}
