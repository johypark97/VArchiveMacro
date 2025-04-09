import com.github.johypark97.varchivemacro.macro.spi.LanguageProvider;
import com.github.johypark97.varchivemacro.macro.spi.LanguageProviderImpl;

module varchivemacro.macro {
    requires java.net.http;
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

    opens com.github.johypark97.varchivemacro.macro.model to javafx.base;
    opens com.github.johypark97.varchivemacro.macro.ui.view to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.ui.view.component to javafx.fxml;

    // libraries
    requires com.github.kwhat.jnativehook;
    requires com.google.common;
    requires com.google.gson;
    requires org.slf4j;

    // xml language resource bundle provider
    provides LanguageProvider with LanguageProviderImpl;
    uses LanguageProvider;

    // exports
    exports com.github.johypark97.varchivemacro.macro.data to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.github.data to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.repository to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.resource to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.ui.presenter to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.ui.view to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro;
}
