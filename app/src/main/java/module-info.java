module varchivemacro {
    requires java.desktop;
    requires java.logging;

    requires com.github.kwhat.jnativehook;
    requires com.google.gson;

    requires org.bytedeco.javacpp;
    requires org.bytedeco.leptonica.windows.x86_64;
    requires org.bytedeco.leptonica;
    requires org.bytedeco.tesseract.windows.x86_64;
    requires org.bytedeco.tesseract;

    exports com.github.johypark97.varchivemacro.config to com.google.gson;
    exports com.github.johypark97.varchivemacro.gui.model.datastruct to com.google.gson;

    exports com.github.johypark97.varchivemacro;
}
