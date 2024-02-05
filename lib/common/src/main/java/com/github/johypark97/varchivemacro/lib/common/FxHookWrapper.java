package com.github.johypark97.varchivemacro.lib.common;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

public class FxHookWrapper {
    private static final Logger LOGGER =
            Logger.getLogger(GlobalScreen.class.getPackage().getName());

    public static void disableLogging() {
        LOGGER.setLevel(Level.OFF);
        LOGGER.setUseParentHandlers(false);
    }

    public static void setEventDispatcher() {
        GlobalScreen.setEventDispatcher(new JavaFxDispatchService());
    }

    public static boolean isRegistered() {
        return GlobalScreen.isNativeHookRegistered();
    }

    public static void register() throws NativeHookException {
        GlobalScreen.registerNativeHook();
    }

    public static void unregister() throws NativeHookException {
        GlobalScreen.unregisterNativeHook();
    }

    public static void addKeyListener(NativeKeyListener listener) {
        GlobalScreen.addNativeKeyListener(listener);
    }

    public static void removeKeyListener(NativeKeyListener listener) {
        GlobalScreen.removeNativeKeyListener(listener);
    }

    public static class JavaFxDispatchService extends SwingDispatchService {
        @Override
        public void execute(Runnable r) {
            Platform.runLater(r);
        }
    }
}
