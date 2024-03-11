module varchivemacro.lib.desktop {
    requires java.desktop;

    // 3rd party
    requires com.google.common;

    // exports
    exports com.github.johypark97.varchivemacro.lib.desktop.gui.component;
    exports com.github.johypark97.varchivemacro.lib.desktop.gui.util;
    exports com.github.johypark97.varchivemacro.lib.desktop.gui.viewmodel;

    exports com.github.johypark97.varchivemacro.lib.desktop;
}
