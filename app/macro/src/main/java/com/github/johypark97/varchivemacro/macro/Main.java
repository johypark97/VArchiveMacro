package com.github.johypark97.varchivemacro.macro;

import static com.github.johypark97.varchivemacro.lib.common.gui.util.SwingLookAndFeel.setSystemLookAndFeel;

import com.github.johypark97.varchivemacro.lib.common.image.ImageConverter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.ExpectedPresenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.LicensePresenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.MacroPresenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.ScannerTaskPresenter;
import com.github.johypark97.varchivemacro.macro.gui.view.ExpectedView;
import com.github.johypark97.varchivemacro.macro.gui.view.LicenseView;
import com.github.johypark97.varchivemacro.macro.gui.view.MacroView;
import com.github.johypark97.varchivemacro.macro.gui.view.ScannerTaskView;
import javax.swing.SwingUtilities;

public class Main {
    private final MacroPresenter macroPresenter = new MacroPresenter(MacroView.class);

    public static void main(String[] args) {
        ImageConverter.disableDiskCache();

        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();

            Main main = new Main();
            main.macroPresenter.start();
        });
    }

    private Main() {
        macroPresenter.setPresenters(new ExpectedPresenter(ExpectedView.class),
                new LicensePresenter(LicenseView.class),
                new ScannerTaskPresenter(ScannerTaskView.class));
    }
}
