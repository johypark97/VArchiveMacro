import com.github.johypark97.varchivemacro.macro.spi.LanguageProvider;
import com.github.johypark97.varchivemacro.macro.spi.LanguageProviderImpl;

module varchivemacro.macro {
    requires varchivemacro.lib.common;
    requires varchivemacro.lib.desktop;
    requires varchivemacro.lib.hook;
    requires varchivemacro.lib.jfx;
    requires varchivemacro.lib.scanner;

    // javafx
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens com.github.johypark97.varchivemacro.macro.fxgui.model.manager to javafx.base;
    opens com.github.johypark97.varchivemacro.macro.fxgui.view.component to javafx.fxml;

    // 3rd party
    requires com.github.kwhat.jnativehook;
    requires com.google.common;
    requires com.google.gson;
    requires java.sql;
    requires org.slf4j;

    exports com.github.johypark97.varchivemacro.macro.fxgui.model.data to com.google.gson;

    // xml language resource bundle provider
    provides LanguageProvider with LanguageProviderImpl;
    uses LanguageProvider;

    // exports
    exports com.github.johypark97.varchivemacro.macro.fxgui.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.fxgui.model.service to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro;
}
