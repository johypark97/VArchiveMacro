module varchivemacro.macro {
    requires varchivemacro.lib.common;
    requires varchivemacro.lib.scanner;

    // 3rd party
    requires com.github.kwhat.jnativehook;
    requires com.google.common;
    requires com.google.gson;
    requires org.slf4j;

    exports com.github.johypark97.varchivemacro.macro.gui.model.datastruct to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro;
}