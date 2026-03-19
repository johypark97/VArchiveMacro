import com.github.johypark97.varchivemacro.macro.spi.LanguageProvider;
import com.github.johypark97.varchivemacro.macro.spi.LanguageProviderImpl;

module varchivemacro.macro {
    requires java.net.http;
    requires java.sql;

    requires varchivemacro.libcommon;
    requires varchivemacro.libdesktop;
    requires varchivemacro.libhook;
    requires varchivemacro.libjfx;
    requires varchivemacro.lib.scanner;

    // javafx
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens com.github.johypark97.varchivemacro.macro.ui.mvp.view to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel to javafx.base;

    // libraries
    requires com.github.kwhat.jnativehook;
    requires com.github.zafarkhaja.semver;
    requires com.google.common;
    requires com.google.gson;
    requires io.reactivex.rxjava3;
    requires org.slf4j;

    exports com.github.johypark97.varchivemacro.macro.common.config.storage.dto to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.common.github.infra.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.common.license.infra.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.common.programdata.infra.model to com.google.gson;

    // xml language resource bundle provider
    provides LanguageProvider with LanguageProviderImpl;
    uses LanguageProvider;

    // exports
    exports com.github.johypark97.varchivemacro.macro.ui.mvp.presenter to varchivemacro.libjfx;
    exports com.github.johypark97.varchivemacro.macro.ui.mvp.view to varchivemacro.libjfx;
    exports com.github.johypark97.varchivemacro.macro;
}
