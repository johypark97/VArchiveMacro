package com.github.johypark97.varchivemacro.macro.gui.presenter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;

public interface IMacro {
    interface Presenter {
        void start();

        void stop();

        void viewOpened();

        void viewClosed();

        void openLicenseView(JFrame frame);

        void loadServerRecord(String djName);

        void recordViewerTreeNodeSelected(Object object);

        void openExpected(JFrame frame);

        void showScannerTask(JFrame frame, int taskNumber);
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void disposeView();

        void addLog(String message);

        void showErrorDialog(String message);

        void showMessageDialog(String title, String message);

        void setRecordViewerTreeModel(TreeModel model);

        void showRecord(String text, List<Float> records);

        void setSelectableDlcs(Map<String, String> codeNameMap);

        Set<String> getSelectedDlcs();

        void setScannerTaskTableModel(TableModel model);
    }
}
