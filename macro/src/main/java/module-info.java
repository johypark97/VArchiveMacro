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

    opens com.github.johypark97.varchivemacro.macro.ui.mvp.view to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel to javafx.base;

    // libraries
    requires com.github.kwhat.jnativehook;
    requires com.google.common;
    requires com.google.gson;
    requires io.reactivex.rxjava3;
    requires org.slf4j;

    exports com.github.johypark97.varchivemacro.macro.common.config.domain.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.common.config.infra.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.common.license.infra.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.common.programdata.infra.model to com.google.gson;

    // xml language resource bundle provider
    provides LanguageProvider with LanguageProviderImpl;
    uses LanguageProvider;

    // exports
    exports com.github.johypark97.varchivemacro.macro.ui.mvp.presenter to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.ui.mvp.view to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro;
}
