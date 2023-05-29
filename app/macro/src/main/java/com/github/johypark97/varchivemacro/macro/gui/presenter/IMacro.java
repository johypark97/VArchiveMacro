package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.ScannerResultListViewModels;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.ScannerResultListViewModels.ScannerResultListViewModel;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.ScannerTaskListViewModels.ColumnKey;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.TableModelWithLookup;
import com.google.common.collect.Table;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreeModel;

public interface IMacro {
    interface Presenter {
        void start();

        void stop();

        void viewOpened();

        void viewClosed();

        void openLicenseView(JFrame frame);

        void changeLanguage(Locale locale);

        void loadServerRecord(String djName);

        void recordViewerTreeNodeSelected(Object object);

        void openExpected(JFrame frame);

        void showScannerTask(JFrame frame, int taskNumber);

        void analyzeScannerTask();

        void refreshScannerResult();

        void uploadRecord(Path accountPath);

        void stopCommand();
    }


    interface View extends MacroViewConfig {
        void setPresenter(Presenter presenter);

        void showView();

        void disposeView();

        void addLog(String message);

        void showErrorDialog(String message);

        void showMessageDialog(String title, String message);

        void setRecordViewerTreeModel(TreeModel model);

        void showRecord(String text, Table<Button, Pattern, String> records);

        void setSelectableDlcTabs(List<String> tabs);

        void setScannerTaskTableModel(TableModelWithLookup<ColumnKey> model);

        void setScannerResultTableModel(
                TableModelWithLookup<ScannerResultListViewModels.ColumnKey> model);

        void setScannerResultTableRowSorter(TableRowSorter<ScannerResultListViewModel> rowSorter);
    }
}
