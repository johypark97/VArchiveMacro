module varchivemacro.macro {
    requires varchivemacro.lib.common;

    // guava
    requires com.google.common;

    // slf4j
    requires org.slf4j;

    exports com.github.johypark97.varchivemacro.macro.gui.model.datastruct to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro;
}
