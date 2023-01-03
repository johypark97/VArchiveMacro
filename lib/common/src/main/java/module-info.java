module varchivemacro.lib.common {
    requires java.logging;
    requires transitive java.desktop;

    requires transitive com.github.kwhat.jnativehook;
    requires transitive com.google.gson;

    exports com.github.johypark97.varchivemacro.lib.common.database.datastruct;
    exports com.github.johypark97.varchivemacro.lib.common.database.util;
    exports com.github.johypark97.varchivemacro.lib.common.gui.component;
    exports com.github.johypark97.varchivemacro.lib.common.gui.util;
    exports com.github.johypark97.varchivemacro.lib.common.hook;
    exports com.github.johypark97.varchivemacro.lib.common.json;
    exports com.github.johypark97.varchivemacro.lib.common.resource;
}
