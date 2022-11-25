package com.github.johypark97.varchivemacro;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.github.johypark97.varchivemacro.config.ConfigManager;
import com.github.johypark97.varchivemacro.gui.model.LicenseModel;
import com.github.johypark97.varchivemacro.gui.model.SettingsModel;
import com.github.johypark97.varchivemacro.gui.presenter.LicensePresenter;
import com.github.johypark97.varchivemacro.gui.presenter.MacroPresenter;
import com.github.johypark97.varchivemacro.gui.view.LicenseView;
import com.github.johypark97.varchivemacro.gui.view.MacroView;

public class Main {
    // model
    private LicenseModel licenseModel = new LicenseModel();
    private SettingsModel settingsModel = new SettingsModel();

    // macro
    private MacroView macroView = new MacroView();
    private MacroPresenter macroPresenter = new MacroPresenter();

    // license
    private LicenseView licenseView = new LicenseView();
    private LicensePresenter licensePresenter = new LicensePresenter();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setLookAndFeel();

            Main main = new Main();
            main.macroView.showView();
        });
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }

    private Main() {
        settingsModel.subscribe(ConfigManager.getInstance());

        macroPresenter.settingsModel = settingsModel;
        macroPresenter.licenseView = licenseView;
        macroPresenter.macroView = macroView;
        macroView.presenter = macroPresenter;

        licensePresenter.licenseModel = licenseModel;
        licensePresenter.licenseView = licenseView;
        licenseView.presenter = licensePresenter;

        macroPresenter.prepareView();
    }
}
