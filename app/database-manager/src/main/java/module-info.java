module varchivemacro.dbmanager {
    requires varchivemacro.lib.common;

    // javafx
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data to javafx.base;
    opens com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component to javafx.fxml;

    // guava
    requires com.google.common;

    // slf4j
    requires org.slf4j;

    exports com.github.johypark97.varchivemacro.dbmanager;
}
