module varchivemacro.lib.common {
    requires java.logging;
    requires java.net.http;
    requires transitive java.desktop;

    requires com.google.common;
    requires transitive com.github.kwhat.jnativehook;
    requires transitive com.google.gson;

    // tesseract-platform
    requires org.bytedeco.javacpp.windows.x86_64;
    requires org.bytedeco.leptonica.windows.x86_64;
    requires org.bytedeco.tesseract.windows.x86_64;

    exports com.github.johypark97.varchivemacro.lib.common.database.comparator;
    exports com.github.johypark97.varchivemacro.lib.common.database.datastruct to com.google.gson;
    exports com.github.johypark97.varchivemacro.lib.common.database;

    exports com.github.johypark97.varchivemacro.lib.common.gui.component;
    exports com.github.johypark97.varchivemacro.lib.common.gui.util;
    exports com.github.johypark97.varchivemacro.lib.common.gui.viewmodel;

    exports com.github.johypark97.varchivemacro.lib.common.api;
    exports com.github.johypark97.varchivemacro.lib.common.area;
    exports com.github.johypark97.varchivemacro.lib.common.ocr;
    exports com.github.johypark97.varchivemacro.lib.common.protocol;
    exports com.github.johypark97.varchivemacro.lib.common.recognizer;
    exports com.github.johypark97.varchivemacro.lib.common.resource;

    exports com.github.johypark97.varchivemacro.lib.common;
}
