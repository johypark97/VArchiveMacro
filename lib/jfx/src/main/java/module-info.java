module varchivemacro.lib.jfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.github.kwhat.jnativehook;

    exports com.github.johypark97.varchivemacro.lib.jfx.fxgui;
    exports com.github.johypark97.varchivemacro.lib.jfx.mvp;

    exports com.github.johypark97.varchivemacro.lib.jfx;
}
