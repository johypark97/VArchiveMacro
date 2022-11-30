package com.github.johypark97.varchivemacro.lib.hook;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class HookWrapper {
    public static void disableLogging() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
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
