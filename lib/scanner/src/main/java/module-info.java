module varchivemacro.lib.scanner {
    requires java.desktop;
    requires java.net.http;

    requires varchivemacro.lib.common;

    // 3rd party
    requires com.google.gson;
    requires com.google.common;

    // tesseract-platform
    requires org.bytedeco.javacpp.windows.x86_64;
    requires org.bytedeco.leptonica.windows.x86_64;
    requires org.bytedeco.tesseract.windows.x86_64;

    // exports
    exports com.github.johypark97.varchivemacro.lib.scanner.api;
    exports com.github.johypark97.varchivemacro.lib.scanner.area;
    exports com.github.johypark97.varchivemacro.lib.scanner.database.comparator;
    exports com.github.johypark97.varchivemacro.lib.scanner.database.datastruct to com.google.gson;
    exports com.github.johypark97.varchivemacro.lib.scanner.database;
    exports com.github.johypark97.varchivemacro.lib.scanner.ocr;
    exports com.github.johypark97.varchivemacro.lib.scanner.recognizer;
    exports com.github.johypark97.varchivemacro.lib.scanner;
}
