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

    opens com.github.johypark97.varchivemacro.macro.domain.scanner.model to javafx.base;

    // libraries
    requires com.github.kwhat.jnativehook;
    requires com.google.common;
    requires com.google.gson;
    requires org.slf4j;

    exports com.github.johypark97.varchivemacro.macro.infrastructure.config.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.infrastructure.data.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.infrastructure.github.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.infrastructure.license.model to com.google.gson;

    // xml language resource bundle provider
    provides LanguageProvider with LanguageProviderImpl;
    uses LanguageProvider;

    // exports
    exports com.github.johypark97.varchivemacro.macro;
}
