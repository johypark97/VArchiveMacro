package com.github.johypark97.varchivemacro.lib.hook;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HookWrapper {
    private static final Logger LOGGER =
            Logger.getLogger(GlobalScreen.class.getPackage().getName());

    public static void disableLogging() {
        LOGGER.setLevel(Level.OFF);
        LOGGER.setUseParentHandlers(false);
    }

    public static void setSwingEventDispatcher() {
        GlobalScreen.setEventDispatcher(new SwingDispatchService());
    }

    public static void register() throws NativeHookException {
        if (!GlobalScreen.isNativeHookRegistered()) {
            GlobalScreen.registerNativeHook();
        }
    }

    public static void unregister() throws NativeHookException {
        if (GlobalScreen.isNativeHookRegistered()) {
            GlobalScreen.unregisterNativeHook();
        }
    }

    public static void addKeyListener(NativeKeyListener listener) {
        GlobalScreen.addNativeKeyListener(listener);
    }

    public static void removeKeyListener(NativeKeyListener listener) {
        GlobalScreen.removeNativeKeyListener(listener);
    }
}
