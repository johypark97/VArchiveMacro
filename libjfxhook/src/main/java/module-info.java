module varchivemacro.libjfxhook {
    requires varchivemacro.libdesktop;

    // javafx
    requires javafx.graphics;

    // libraries
    requires com.github.kwhat.jnativehook;
    requires org.slf4j;

    // exports
    exports com.github.johypark97.varchivemacro.libjfxhook.converter;
    exports com.github.johypark97.varchivemacro.libjfxhook.domain.event;
    exports com.github.johypark97.varchivemacro.libjfxhook.domain.exception;
    exports com.github.johypark97.varchivemacro.libjfxhook.locator to com.github.kwhat.jnativehook;
    exports com.github.johypark97.varchivemacro.libjfxhook;
}
