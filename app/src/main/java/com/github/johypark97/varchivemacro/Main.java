package com.github.johypark97.varchivemacro;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.github.johypark97.varchivemacro.config.ConfigManager;
import com.github.johypark97.varchivemacro.model.LicenseModel;
import com.github.johypark97.varchivemacro.model.MacroModel;
import com.github.johypark97.varchivemacro.presenter.LicensePresenter;
import com.github.johypark97.varchivemacro.presenter.MacroPresenter;
import com.github.johypark97.varchivemacro.view.LicenseView;
import com.github.johypark97.varchivemacro.view.MacroView;

public class Main {
    private MacroModel macroModel = new MacroModel();
    private MacroView macroView = new MacroView();
    private MacroPresenter macroPresenter = new MacroPresenter();

    private LicenseModel licenseModel = new LicenseModel();
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
        macroPresenter.model = macroModel;
        macroPresenter.view = macroView;
        macroView.presenter = macroPresenter;

        macroPresenter.licenseView = licenseView;

        macroModel.subscribe(ConfigManager.getInstance());
        macroPresenter.prepareView();

        licensePresenter.model = licenseModel;
        licensePresenter.view = licenseView;
        licenseView.presenter = licensePresenter;
    }
}
