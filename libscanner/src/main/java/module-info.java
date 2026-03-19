module varchivemacro.libscanner {
    requires java.desktop;
    requires java.net.http;

    requires varchivemacro.libcommon;

    // libraries
    requires com.google.common;
    requires com.google.gson;
    requires org.xerial.sqlitejdbc;

    // tesseract-platform
    requires org.bytedeco.javacpp.windows.x86_64;
    requires org.bytedeco.leptonica.windows.x86_64;
    requires org.bytedeco.tesseract.windows.x86_64;

    // exports
    exports com.github.johypark97.varchivemacro.libscanner.api;
    exports com.github.johypark97.varchivemacro.libscanner.area;
    exports com.github.johypark97.varchivemacro.libscanner.database.comparator;
    exports com.github.johypark97.varchivemacro.libscanner.database.datastruct to com.google.gson;
    exports com.github.johypark97.varchivemacro.libscanner.database;
    exports com.github.johypark97.varchivemacro.libscanner.ocr;
    exports com.github.johypark97.varchivemacro.libscanner.recognizer;
    exports com.github.johypark97.varchivemacro.libscanner;
}
