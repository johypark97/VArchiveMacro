package com.github.johypark97.varchivemacro.macro;

import static com.github.johypark97.varchivemacro.lib.common.gui.util.SwingLookAndFeel.setSystemLookAndFeel;
import javax.swing.SwingUtilities;
import com.github.johypark97.varchivemacro.macro.config.ConfigManager;
import com.github.johypark97.varchivemacro.macro.gui.model.LicenseModel;
import com.github.johypark97.varchivemacro.macro.gui.model.SettingsModel;
import com.github.johypark97.varchivemacro.macro.gui.presenter.LicensePresenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.MacroPresenter;
import com.github.johypark97.varchivemacro.macro.gui.view.LicenseView;
import com.github.johypark97.varchivemacro.macro.gui.view.MacroView;

public class Main {
    // macro
    private MacroPresenter macroPresenter = new MacroPresenter(new MacroView());

    // license
    private LicensePresenter licensePresenter = new LicensePresenter(new LicenseView());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();

            Main main = new Main();
            main.macroPresenter.start();
        });
    }

    private Main() {
        // link presenter - presenter
        macroPresenter.licensePresenter = licensePresenter;

        // link macro presenter - model
        SettingsModel settingsModel = new SettingsModel();
        settingsModel.subscribe(ConfigManager.getInstance());
        macroPresenter.settingsModel = settingsModel;

        // link license presenter - model
        licensePresenter.licenseModel = new LicenseModel();
    }
}
