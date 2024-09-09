module varchivemacro.dbmanager {
    requires java.sql;

    requires varchivemacro.lib.common;
    requires varchivemacro.lib.desktop;
    requires varchivemacro.lib.hook;
    requires varchivemacro.lib.jfx;
    requires varchivemacro.lib.scanner;

    // javafx
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data to javafx.base;
    opens com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component to javafx.fxml;

    // libraries
    requires com.github.kwhat.jnativehook;
    requires com.google.common;
    requires com.google.gson;
    requires org.slf4j;

    // exports
    exports com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service to varchivemacro.lib.jfx;

    exports com.github.johypark97.varchivemacro.dbmanager;
}
