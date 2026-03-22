package com.github.johypark97.varchivemacro.libjfxhook;

import com.github.johypark97.varchivemacro.libjfxhook.infra.adapter.KeyListenerAdapter;
import com.github.johypark97.varchivemacro.libjfxhook.domain.event.JfxHookKeyListener;
import com.github.johypark97.varchivemacro.libjfxhook.domain.event.JfxHookEventSubscription;
import com.github.johypark97.varchivemacro.libjfxhook.domain.exception.JfxHookException;
import com.github.johypark97.varchivemacro.libjfxhook.locator.JLinkNativeLibraryLocator;
import com.github.johypark97.varchivemacro.libjfxhook.service.JfxDispatchService;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JfxHook {
    private static final Logger LOGGER =
            Logger.getLogger(GlobalScreen.class.getPackage().getName());

    public static void useJLinkNativeLibraryLocator() {
        JLinkNativeLibraryLocator.use();
    }

    public static void disableLogging() {
        LOGGER.setLevel(Level.OFF);
        LOGGER.setUseParentHandlers(false);
    }

    public static void setEventDispatcher() {
        GlobalScreen.setEventDispatcher(new JfxDispatchService());
    }

    public static boolean isRegistered() {
        return GlobalScreen.isNativeHookRegistered();
    }

    public static void register() throws JfxHookException {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new JfxHookException(e);
        }
    }

    public static void unregister() throws JfxHookException {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            throw new JfxHookException(e);
        }
    }

    public static JfxHookEventSubscription subscribe(JfxHookKeyListener listener) {
        KeyListenerAdapter adapter = new KeyListenerAdapter(listener);
        GlobalScreen.addNativeKeyListener(adapter);
        return () -> GlobalScreen.removeNativeKeyListener(adapter);
    }
}
