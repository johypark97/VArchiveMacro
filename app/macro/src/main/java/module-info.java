module varchivemacro.macro {
    requires java.desktop;

    requires varchivemacro.lib.common;
    requires varchivemacro.lib.hook;
    requires varchivemacro.lib.json;

    requires org.bytedeco.javacpp;
    requires org.bytedeco.leptonica.windows.x86_64;
    requires org.bytedeco.leptonica;
    requires org.bytedeco.tesseract.windows.x86_64;
    requires org.bytedeco.tesseract;

    exports com.github.johypark97.varchivemacro.macro.config to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.gui.model.datastruct to com.google.gson;

    exports com.github.johypark97.varchivemacro.macro;
}