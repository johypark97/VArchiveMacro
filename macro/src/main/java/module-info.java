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

    opens com.github.johypark97.varchivemacro.macro.fxgui.model.manager to javafx.base;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.captureviewer to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.home to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.component to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor to javafx.fxml;
    opens com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense to javafx.fxml;

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
    exports com.github.johypark97.varchivemacro.macro.fxgui.model to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.fxgui.model.service to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.captureviewer to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.home to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.component to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense to varchivemacro.lib.jfx;
    exports com.github.johypark97.varchivemacro.macro.github.data to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro.resource to com.google.gson;
    exports com.github.johypark97.varchivemacro.macro;
}
