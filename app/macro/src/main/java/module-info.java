module varchivemacro.macro {
    requires varchivemacro.lib.common;

    // guava
    requires com.google.common;

    // tesseract-platform
    requires org.bytedeco.javacpp.windows.x86_64;
    requires org.bytedeco.leptonica.windows.x86_64;
    requires org.bytedeco.tesseract.windows.x86_64;

    // slf4j
    requires org.slf4j;

    exports com.github.johypark97.varchivemacro.macro;
}
