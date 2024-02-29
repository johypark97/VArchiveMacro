module varchivemacro.lib.common {
    requires com.github.kwhat.jnativehook;
    requires com.google.common;
    requires com.google.gson;

    exports com.github.johypark97.varchivemacro.lib.common.gui.component;
    exports com.github.johypark97.varchivemacro.lib.common.gui.util;
    exports com.github.johypark97.varchivemacro.lib.common.gui.viewmodel;

    exports com.github.johypark97.varchivemacro.lib.common.protocol;
    exports com.github.johypark97.varchivemacro.lib.common.resource;
    exports com.github.johypark97.varchivemacro.lib.common.service;

    exports com.github.johypark97.varchivemacro.lib.common;
}
