package com.github.johypark97.varchivemacro.macro;

import static com.github.johypark97.varchivemacro.lib.common.gui.util.SwingLookAndFeel.setSystemLookAndFeel;

import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.macro.core.backend.Backend;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ScannerResultListModel;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ScannerTaskListModel;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.ScannerTaskModel;
import com.github.johypark97.varchivemacro.macro.gui.model.SongRecordModels.SongRecordModel;
import com.github.johypark97.varchivemacro.macro.gui.presenter.ExpectedPresenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.LicensePresenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.MacroPresenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.ScannerTaskPresenter;
import com.github.johypark97.varchivemacro.macro.gui.view.ExpectedView;
import com.github.johypark97.varchivemacro.macro.gui.view.LicenseView;
import com.github.johypark97.varchivemacro.macro.gui.view.MacroView;
import com.github.johypark97.varchivemacro.macro.gui.view.ScannerTaskView;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    private final Backend backend = new Backend();
    private final MacroPresenter macroPresenter = new MacroPresenter(MacroView.class);

    private Main() {
        backend.addObserver(macroPresenter);
        macroPresenter.setBackend(backend);

        ScannerTaskModel scannerTaskModel = new ScannerTaskModel(backend.getTaskDataProvider());
        SongRecordModel songRecordModel = new SongRecordModel(backend.getSongRecordManager());

        ScannerTaskListModel scannerTaskListModel = new ScannerTaskListModel();
        backend.addTaskListObserver(scannerTaskListModel);
        scannerTaskListModel.linkTaskListProvider(backend.getTaskListProvider());

        ScannerResultListModel scannerResultListModel = new ScannerResultListModel();
        backend.addResultListObserver(scannerResultListModel);
        scannerResultListModel.linkResultListProvider(backend.getResultListProvider());

        macroPresenter.setModels(songRecordModel, scannerTaskModel, scannerTaskListModel,
                scannerResultListModel);

        macroPresenter.setPresenters(new ExpectedPresenter(ExpectedView.class),
                new LicensePresenter(LicenseView.class),
                new ScannerTaskPresenter(ScannerTaskView.class));
    }

    public static void main(String[] args) {
        ImageConverter.disableDiskCache();

        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();

            try {
                Language.init();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Main main = new Main();
            main.macroPresenter.start();
        });
    }
}
