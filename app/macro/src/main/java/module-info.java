module varchivemacro.macro {
    requires varchivemacro.lib.common;

    requires org.bytedeco.javacpp;
    requires org.bytedeco.leptonica.windows.x86_64;
    requires org.bytedeco.leptonica;
    requires org.bytedeco.tesseract.windows.x86_64;
    requires org.bytedeco.tesseract;

    exports com.github.johypark97.varchivemacro.macro;
}
