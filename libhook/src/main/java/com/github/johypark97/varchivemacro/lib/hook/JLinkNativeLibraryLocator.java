package com.github.johypark97.varchivemacro.lib.hook;

import com.github.kwhat.jnativehook.DefaultLibraryLocator;
import com.github.kwhat.jnativehook.NativeLibraryLocator;
import java.io.File;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JLinkNativeLibraryLocator implements NativeLibraryLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JLinkNativeLibraryLocator.class);

    public static void use() {
        System.setProperty("jnativehook.lib.locator",
                JLinkNativeLibraryLocator.class.getCanonicalName());
    }

    @Override
    public Iterator<File> getLibraries() {
        NativeLibraryLocator locator;

        if (JrtNativeLibraryLocator.isHookInJrt()) {
            LOGGER.atDebug().log("use JrtNativeLibraryLocator");
            locator = new JrtNativeLibraryLocator();
        } else {
            LOGGER.atDebug().log("use the default NativeLibraryLocator");
            locator = new DefaultLibraryLocator();
        }

        return locator.getLibraries();
    }
}
