package com.github.johypark97.varchivemacro.macro.gui.view;

import com.github.johypark97.varchivemacro.lib.common.gui.component.CheckboxGroup;
import com.github.johypark97.varchivemacro.lib.common.gui.util.ComponentSize;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.View;
import com.github.johypark97.varchivemacro.macro.util.BuildInfo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serial;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

public class MacroView extends JFrame implements View, WindowListener {
    @Serial
    private static final long serialVersionUID = -4985735753645144141L;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final String WINDOW_TITLE = "V-ARCHIVE Macro";
    private static final int LOG_FONT_SIZE = 12;
    private static final int LOG_LINES = 100;
    private static final int LOG_ROWS = 8;
    private static final int WINDOW_HEIGHT = 600;
    private static final int WINDOW_WIDTH = 800;
    public static final Set<String> DEFAULT_DLCS = Set.of("RESPECT", "PORTABLE 1", "PORTABLE 2");

    // presenter
    public transient Presenter presenter;

    // components
    private JMenu menuFile;
    private JMenu menuInfo;
    protected JMenuItem menuItemAbout;
    protected JMenuItem menuItemExit;
    protected JMenuItem menuItemOSL;

    private JTextArea recordViewerTextArea;
    private JTree recordViewerTree;
    private final List<JTextField> recordViewerGridTextFields = new ArrayList<>(16);
    protected JButton loadRemoteRecordButton;

    private JScrollPane dlcCheckboxScrollPane;
    private JTextArea logTextArea;
    private JTextField accountFileTextField;
    protected JButton selectAccountFileButton;
    protected JButton selectAllDlcButton;
    protected JButton showExpectedButton;
    protected JButton showScannerTaskButton;
    protected JButton unselectAllDlcButton;
    protected JButton uploadRecordButton;
    protected JTable scannerTable;
    protected transient CheckboxGroup<String> dlcCheckboxGroup = new CheckboxGroup<>();

    // event listeners
    private transient final ActionListener buttonListener = new MacroViewButtonListener(this);
    private transient final ActionListener menuListener = new MacroViewMenuListener(this);

    public MacroView() {
        super(WINDOW_TITLE + " v" + BuildInfo.version);

        setFrameOption();
        setContentPanel();
        setContent();
        setText();

        addWindowListener(this);
    }

    private void setFrameOption() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2));
        setResizable(true);
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        setLocationRelativeTo(null);

        URL url = getClass().getResource("/overMeElFail.png");
        if (url != null) {
            setIconImage(new ImageIcon(url).getImage());
        }
    }

    private void setContentPanel() {
        setContentPane(new JPanel(new BorderLayout()));
    }

    private void setContent() {
        add(createMenu(), BorderLayout.PAGE_START);
        add(createCenter(), BorderLayout.CENTER);
        add(createLog(), BorderLayout.PAGE_END);
    }

    private void setText() {
        menuFile.setText("File");
        menuInfo.setText("Info");
        menuItemAbout.setText("About");
        menuItemExit.setText("Exit");
        menuItemOSL.setText("Open Source License");
    }

    private Component createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // menu file
        menuFile = new JMenu();
        {
            menuItemExit = new JMenuItem();
            menuItemExit.addActionListener(menuListener);
            menuFile.add(menuItemExit);
        }
        menuBar.add(menuFile);

        // menu info
        menuInfo = new JMenu();
        {
            menuItemOSL = new JMenuItem();
            menuItemOSL.addActionListener(menuListener);
            menuInfo.add(menuItemOSL);

            menuInfo.addSeparator();

            menuItemAbout = new JMenuItem();
            menuItemAbout.addActionListener(menuListener);
            menuInfo.add(menuItemAbout);
        }
        menuBar.add(menuInfo);

        return menuBar;
    }

    private Component createCenter() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Record Viewer", createViewerTab());
        tabbedPane.addTab("Scanner", createScannerTab());
        tabbedPane.addTab("Macro (WIP)", createMacroTab());

        return tabbedPane;
    }

    private Component createViewerTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // tool box
        Box toolBox = Box.createHorizontalBox();
        {
            toolBox.add(Box.createHorizontalGlue());

            loadRemoteRecordButton = new JButton("Load server record");
            loadRemoteRecordButton.addActionListener(buttonListener);
            toolBox.add(loadRemoteRecordButton);

            toolBox.add(Box.createHorizontalGlue());
        }
        panel.add(toolBox, BorderLayout.PAGE_START);

        // record viewer tree
        JScrollPane viewerScrollPane;
        {
            recordViewerTree = new JTree();
            recordViewerTree.setModel(null);
            recordViewerTree.getSelectionModel()
                    .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

            recordViewerTree.getSelectionModel().addTreeSelectionListener(
                    (e) -> presenter.recordViewerTreeNodeSelected(
                            e.getPath().getLastPathComponent()));

            viewerScrollPane = new JScrollPane(recordViewerTree);
            ComponentSize.setPreferredWidth(viewerScrollPane, 300);
        }
        panel.add(viewerScrollPane, BorderLayout.LINE_START);

        // record viewer detail
        JPanel detailPanel = new JPanel(new BorderLayout());
        {
            // text area
            recordViewerTextArea = new JTextArea();
            recordViewerTextArea.setEditable(false);
            detailPanel.add(new JScrollPane(recordViewerTextArea), BorderLayout.CENTER);

            // grid
            JPanel grid = new JPanel();
            grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 20));
            {
                List<String> columns = List.of("", "4B", "5B", "6B", "8B");
                List<String> rows = List.of("", "NM", "HD", "MX", "SC");
                grid.setLayout(new GridLayout(columns.size(), rows.size()));

                rows.forEach((row) -> columns.forEach((column) -> {
                    if (row.isBlank()) {
                        JLabel label = new JLabel();
                        if (!column.isBlank()) {
                            label.setText(column);
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                        }
                        grid.add(label);
                    } else {
                        if (column.isBlank()) {
                            JLabel label = new JLabel(row);
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                            grid.add(label);
                        } else {
                            JTextField textField = new JTextField();
                            textField.setBackground(Color.WHITE);
                            textField.setEditable(false);
                            recordViewerGridTextFields.add(textField);

                            grid.add(textField);
                        }
                    }
                }));
            }
            detailPanel.add(grid, BorderLayout.PAGE_END);
        }
        panel.add(detailPanel, BorderLayout.CENTER);

        return panel;
    }

    private Component createScannerTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // tool box
        Box toolBox = Box.createHorizontalBox();
        {
            toolBox.add(new JLabel("Account file : "));

            accountFileTextField = new JTextField();
            accountFileTextField.setBackground(Color.WHITE);
            accountFileTextField.setEditable(false);
            ComponentSize.expandWidthOnly(accountFileTextField);
            toolBox.add(accountFileTextField);

            selectAccountFileButton = new JButton("Select");
            selectAccountFileButton.addActionListener(buttonListener);
            toolBox.add(selectAccountFileButton);
        }
        panel.add(toolBox, BorderLayout.PAGE_START);

        // main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        {
            // scanner table panel
            JScrollPane scannerTableScrollPane;
            {
                scannerTable = new JTable();
                scannerTable.getTableHeader().setReorderingAllowed(false);
                scannerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                scannerTable.setBackground(Color.WHITE);
                scannerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                scannerTableScrollPane = new JScrollPane(scannerTable);
                scannerTableScrollPane.getViewport().setBackground(Color.WHITE);
            }
            mainPanel.add(scannerTableScrollPane, BorderLayout.CENTER);

            // button box
            Box buttonBox = Box.createHorizontalBox();
            {
                showScannerTaskButton = new JButton("show");
                showScannerTaskButton.addActionListener(buttonListener);
                buttonBox.add(showScannerTaskButton);

                buttonBox.add(Box.createHorizontalGlue());

                uploadRecordButton = new JButton("Upload");
                uploadRecordButton.addActionListener(buttonListener);
                buttonBox.add(uploadRecordButton);
            }
            mainPanel.add(buttonBox, BorderLayout.PAGE_END);
        }
        panel.add(mainPanel, BorderLayout.CENTER);

        // dlc panel
        JPanel dlcPanel = new JPanel(new BorderLayout());
        {
            dlcCheckboxScrollPane = new JScrollPane();
            dlcCheckboxScrollPane.getVerticalScrollBar().setUnitIncrement(16);
            dlcCheckboxScrollPane.setBorder(BorderFactory.createTitledBorder("Owned DLC Tabs"));
            dlcPanel.add(dlcCheckboxScrollPane, BorderLayout.CENTER);

            // button box
            Box buttonBox = Box.createVerticalBox();
            {
                Box box = Box.createHorizontalBox();
                {
                    selectAllDlcButton = new JButton("Select all");
                    selectAllDlcButton.addActionListener(buttonListener);
                    box.add(selectAllDlcButton);

                    unselectAllDlcButton = new JButton("Unselect all");
                    unselectAllDlcButton.addActionListener(buttonListener);
                    box.add(unselectAllDlcButton);
                }
                buttonBox.add(box);

                showExpectedButton = new JButton("Show expected");
                showExpectedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                showExpectedButton.addActionListener(buttonListener);
                ComponentSize.expandWidthOnly(showExpectedButton);
                buttonBox.add(showExpectedButton);
            }
            dlcPanel.add(buttonBox, BorderLayout.PAGE_END);
        }
        panel.add(dlcPanel, BorderLayout.LINE_END);

        return panel;
    }

    private Component createMacroTab() {
        return new JPanel();
    }

    private Component createLog() {
        logTextArea = new JTextArea(LOG_ROWS, 0);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, LOG_FONT_SIZE));

        return new JScrollPane(logTextArea);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showView() {
        setVisible(true);
    }

    @Override
    public void disposeView() {
        dispose();
    }

    @Override
    public void addLog(String message) {
        String time = LocalTime.now().format(TIME_FORMATTER);

        SwingUtilities.invokeLater(() -> {
            if (logTextArea.getLineCount() > LOG_LINES) {
                try {
                    int index = logTextArea.getLineEndOffset(0);
                    logTextArea.replaceRange("", 0, index);
                } catch (BadLocationException ignored) {
                }
            }

            logTextArea.append(String.format("[%s] %s%n", time, message));
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }

    @Override
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMessageDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setRecordViewerTreeModel(TreeModel model) {
        recordViewerTree.setModel(model);
    }

    @Override
    public void showRecord(String text, List<Float> records) {
        recordViewerTextArea.setText(text);

        int size = recordViewerGridTextFields.size();
        IntStream.range(0, size).forEach((i) -> {
            // (index % columnSize) + (index / rowSize) * columnSize = sequential index
            // (index / rowSize) + (index % columnSize) * columnSize = transposed index
            int transposedIndex = i / 4 + i % 4 * 4;

            float score = records.get(transposedIndex);
            String value = (score >= 0) ? String.valueOf(score) : "";
            recordViewerGridTextFields.get(i).setText(value);
        });
    }

    @Override
    public void setSelectableDlcTabs(List<String> tabs) {
        dlcCheckboxGroup.clear();
        tabs.forEach((x) -> dlcCheckboxGroup.add(x, x));

        Box box = Box.createVerticalBox();
        dlcCheckboxGroup.forEach((key, checkbox) -> {
            if (DEFAULT_DLCS.contains(key)) {
                checkbox.setEnabled(false);
                checkbox.setSelected(true);
            }
            box.add(checkbox);
        });

        dlcCheckboxScrollPane.setViewportView(box);
        revalidate();
    }

    @Override
    public Set<String> getSelectedDlcTabs() {
        return dlcCheckboxGroup.getSelected();
    }

    @Override
    public void setScannerTaskTableModel(TableModel model) {
        scannerTable.setModel(model);

        TableColumnModel tableColumnModel = scannerTable.getColumnModel();
        tableColumnModel.getColumn(0).setPreferredWidth(40);
        tableColumnModel.getColumn(1).setPreferredWidth(320);
        tableColumnModel.getColumn(2).setPreferredWidth(160);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        presenter.viewOpened();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        presenter.stop();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        presenter.viewClosed();
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}


class MacroViewMenuListener implements ActionListener {
    private final MacroView view;

    public MacroViewMenuListener(MacroView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(view.menuItemExit)) {
            view.presenter.stop();
        } else if (source.equals(view.menuItemOSL)) {
            view.presenter.openLicenseView(view);
        } else {
            view.addLog(source.toString());
        }
    }
}


class MacroViewButtonListener implements ActionListener {
    private final MacroView view;

    public MacroViewButtonListener(MacroView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(view.loadRemoteRecordButton)) {
            String djName = JOptionPane.showInputDialog(view, "Please enter your DJ Name");
            if (djName != null) {
                view.presenter.loadServerRecord(djName);
            }
            // } else if (source.equals(view.selectAccountFileButton)) {
        } else if (source.equals(view.showScannerTaskButton)) {
            int index = view.scannerTable.getSelectedRow();
            if (index != -1) {
                Object value = view.scannerTable.getValueAt(index, 0);
                if (value instanceof Integer taskNumber) {
                    view.presenter.showScannerTask(view, taskNumber);
                }
            }
            // } else if (source.equals(view.uploadRecordButton)) {
        } else if (source.equals(view.selectAllDlcButton)) {
            view.dlcCheckboxGroup.selectAllExclude(MacroView.DEFAULT_DLCS);
        } else if (source.equals(view.unselectAllDlcButton)) {
            view.dlcCheckboxGroup.unselectAllExclude(MacroView.DEFAULT_DLCS);
        } else if (source.equals(view.showExpectedButton)) {
            view.presenter.openExpected(view);
        } else {
            view.addLog(source.toString());
        }
    }
}
