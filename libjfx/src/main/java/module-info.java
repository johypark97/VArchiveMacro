module varchivemacro.libjfx {
    requires varchivemacro.libcommon;

    // javafx
    requires javafx.controls;
    requires javafx.fxml;

    // exports
    exports com.github.johypark97.varchivemacro.libjfx.component;
    exports com.github.johypark97.varchivemacro.libjfx.fxgui;
    exports com.github.johypark97.varchivemacro.libjfx;
}
