module varchivemacro.lib.jfx {
    requires varchivemacro.lib.common;

    // javafx
    requires javafx.controls;
    requires javafx.fxml;

    // exports
    exports com.github.johypark97.varchivemacro.lib.jfx.component;
    exports com.github.johypark97.varchivemacro.lib.jfx.fxgui;
    exports com.github.johypark97.varchivemacro.lib.jfx.mvp;
    exports com.github.johypark97.varchivemacro.lib.jfx;
}
