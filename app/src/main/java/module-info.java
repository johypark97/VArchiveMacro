module varchivemacro {
    requires java.desktop;
    requires java.logging;

    requires com.github.kwhat.jnativehook;
    requires com.google.gson;

    exports com.github.johypark97.varchivemacro.config to com.google.gson;
    exports com.github.johypark97.varchivemacro.model.datastruct to com.google.gson;

    exports com.github.johypark97.varchivemacro;
}
