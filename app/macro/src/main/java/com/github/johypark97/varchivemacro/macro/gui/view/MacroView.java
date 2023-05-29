package com.github.johypark97.varchivemacro.macro.gui.view;

import com.github.johypark97.varchivemacro.lib.common.gui.component.CheckboxGroup;
import com.github.johypark97.varchivemacro.lib.common.gui.component.RadioButtonGroup;
import com.github.johypark97.varchivemacro.lib.common.gui.component.SliderSet;
import com.github.johypark97.varchivemacro.lib.common.gui.util.ComponentSize;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.clientmacro.AnalyzeKey;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.View;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.ScannerResultListViewModels;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.ScannerResultListViewModels.ScannerResultListViewModel;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.ScannerTaskListViewModels.ColumnKey;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.TableColumnLookup;
import com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel.TableModelWithLookup;
import com.github.johypark97.varchivemacro.macro.gui.view.components.UrlLabel;
import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.johypark97.varchivemacro.macro.resource.MacroViewKey;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serial;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
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
    private static final int WINDOW_HEIGHT = 768;
    private static final int WINDOW_WIDTH = 1024;

    private static final Color TABLE_SELECTED_BACKGROUND_COLOR = new Color(0xE0E0E0);
    private static final Color TABLE_SELECTED_FOREGROUND_COLOR = Color.BLACK;
    private static final int TASK_TABLE_HIGHLIGHT_COUNT = 2;

    // presenter
    protected transient Presenter presenter;

    // components
    protected JMenuItem menuAbout;
    protected JMenuItem menuExit;
    protected JMenuItem menuLangEng;
    protected JMenuItem menuLangKor;
    protected JMenuItem menuOSL;

    private JTextArea recordViewerTextArea;
    private JTree recordViewerTree;
    private final Table<Button, Pattern, JTextField> recordViewerGridTextFields =
            HashBasedTable.create();
    protected JButton loadRemoteRecordButton;

    private JScrollPane dlcCheckboxScrollPane;
    private JTextArea logTextArea;
    private transient SliderSet recordUploadDelaySlider;
    private transient SliderSet scannerCaptureDelay;
    private transient SliderSet scannerKeyInputDuration;
    protected JButton analyzeScannerTaskButton;
    protected JButton refreshScannerResultButton;
    protected JButton selectAccountFileButton;
    protected JButton selectAllDlcButton;
    protected JButton selectAllRecordButton;
    protected JButton selectCacheDirectoryButton;
    protected JButton showExpectedButton;
    protected JButton showScannerTaskButton;
    protected JButton stopCommandButton;
    protected JButton unselectAllDlcButton;
    protected JButton unselectAllRecordButton;
    protected JButton uploadRecordButton;
    protected JTable scannerResultTable;
    protected JTable scannerTaskTable;
    protected JTextField accountFileTextField;
    protected JTextField cacheDirTextField;
    protected TableModelWithLookup<ColumnKey> scannerTaskTableModel;
    protected TableModelWithLookup<ScannerResultListViewModels.ColumnKey> scannerResultTableModel;
    protected transient CheckboxGroup<String> dlcCheckboxGroup = new CheckboxGroup<>();

    private transient RadioButtonGroup<AnalyzeKey> macroAnalyzeKey;
    private transient SliderSet macroCaptureDelay;
    private transient SliderSet macroCaptureDuration;
    private transient SliderSet macroCount;
    private transient SliderSet macroKeyInputDuration;

    // event listeners
    private transient final ActionListener buttonListener = new MacroViewButtonListener(this);
    private transient final ActionListener menuListener = new MacroViewMenuListener(this);

    // variables
    private transient final Language lang = Language.getInstance();
    protected transient Path accountPath;
    protected transient Path cacheDirPath;

    public MacroView() {
        super(WINDOW_TITLE + " v" + BuildInfo.version);

        setFrameOption();
        setContentPanel();
        setContent();

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
        add(createBottom(), BorderLayout.PAGE_END);
    }

    private Component createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // menu file
        JMenu menuFile = new JMenu(lang.get(MacroViewKey.MENU_FILE));
        {
            menuExit = new JMenuItem(lang.get(MacroViewKey.MENU_FILE_EXIT));
            menuExit.addActionListener(menuListener);
            menuFile.add(menuExit);
        }
        menuBar.add(menuFile);

        // menu language
        JMenu menuLanguage = new JMenu(lang.get(MacroViewKey.MENU_LANGUAGE));
        {
            List<JMenuItem> list = new LinkedList<>();

            menuLangEng = new JMenuItem(lang.get(MacroViewKey.MENU_LANGUAGE_ENG));
            list.add(menuLangEng);

            menuLangKor = new JMenuItem(lang.get(MacroViewKey.MENU_LANGUAGE_KOR));
            list.add(menuLangKor);

            list.forEach((x) -> {
                x.addActionListener(menuListener);
                menuLanguage.add(x);
            });
        }
        menuBar.add(menuLanguage);

        // menu info
        JMenu menuInfo = new JMenu(lang.get(MacroViewKey.MENU_INFO));
        {
            menuOSL = new JMenuItem(lang.get(MacroViewKey.MENU_INFO_OSL));
            menuOSL.addActionListener(menuListener);
            menuInfo.add(menuOSL);

            menuInfo.addSeparator();

            menuAbout = new JMenuItem(lang.get(MacroViewKey.MENU_INFO_ABOUT));
            menuAbout.addActionListener(menuListener);
            menuInfo.add(menuAbout);
        }
        menuBar.add(menuInfo);

        return menuBar;
    }

    private Component createCenter() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(lang.get(MacroViewKey.TAB_RECORD_VIEWER), createViewerTab());
        tabbedPane.addTab(lang.get(MacroViewKey.TAB_SCANNER), createScannerTab());
        tabbedPane.addTab(lang.get(MacroViewKey.TAB_MACRO), createMacroTab());

        return tabbedPane;
    }

    private Component createViewerTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // tool box
        Box toolBox = Box.createHorizontalBox();
        {
            toolBox.add(Box.createHorizontalGlue());

            loadRemoteRecordButton = new JButton(lang.get(MacroViewKey.LOAD_REMOTE_RECORD_BUTTON));
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
                int rows = Pattern.values().length + 1;
                int columns = Button.values().length + 1;
                grid.setLayout(new GridLayout(rows, columns));

                // header
                grid.add(new JLabel());
                for (Button button : Button.values()) {
                    JLabel label = new JLabel(button + "B");
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    grid.add(label);
                }

                // rows
                for (Pattern pattern : Pattern.values()) {
                    JLabel label = new JLabel(pattern.toString());
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    grid.add(label);

                    for (Button button : Button.values()) {
                        JTextField textField = new JTextField();
                        textField.setBackground(Color.WHITE);
                        textField.setEditable(false);

                        recordViewerGridTextFields.put(button, pattern, textField);
                        grid.add(textField);
                    }
                }
            }
            detailPanel.add(grid, BorderLayout.PAGE_END);
        }
        panel.add(detailPanel, BorderLayout.CENTER);

        return panel;
    }

    private Component createScannerTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // tabbed panel
        JTabbedPane tabbedPane = new JTabbedPane();
        {
            JPanel taskPanel = new JPanel(new BorderLayout());
            {
                // scanner table panel
                JScrollPane scannerTableScrollPane;
                {
                    scannerTaskTable = new JTable();
                    scannerTaskTable.getTableHeader().setReorderingAllowed(false);
                    scannerTaskTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    scannerTaskTable.setBackground(Color.WHITE);
                    scannerTaskTable.setSelectionBackground(TABLE_SELECTED_BACKGROUND_COLOR);
                    scannerTaskTable.setSelectionForeground(TABLE_SELECTED_FOREGROUND_COLOR);
                    scannerTaskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                    scannerTaskTable.setDefaultRenderer(Object.class,
                            new DefaultTableCellRenderer() {
                                @Override
                                public Component getTableCellRendererComponent(JTable table,
                                        Object value, boolean isSelected, boolean hasFocus, int row,
                                        int column) {
                                    int rowIndex = table.convertRowIndexToModel(row);
                                    int indexValue = (int) table.getModel().getValueAt(rowIndex,
                                            scannerTaskTableModel.getTableColumnLookup()
                                                    .getIndex(ColumnKey.INDEX));
                                    int countValue = (int) table.getModel().getValueAt(rowIndex,
                                            scannerTaskTableModel.getTableColumnLookup()
                                                    .getIndex(ColumnKey.COUNT));

                                    Color backgroundColor;
                                    if (countValue - indexValue <= TASK_TABLE_HIGHLIGHT_COUNT) {
                                        backgroundColor = isSelected ? Color.YELLOW : Color.ORANGE;
                                    } else {
                                        backgroundColor = isSelected
                                                ? table.getSelectionBackground()
                                                : table.getBackground();
                                    }

                                    Component component =
                                            super.getTableCellRendererComponent(table, value,
                                                    isSelected, hasFocus, row, column);
                                    component.setBackground(backgroundColor);

                                    return component;
                                }
                            });

                    scannerTableScrollPane = new JScrollPane(scannerTaskTable);
                    scannerTableScrollPane.getViewport().setBackground(Color.WHITE);
                }
                taskPanel.add(scannerTableScrollPane, BorderLayout.CENTER);

                // button box
                Box buttonBox = Box.createHorizontalBox();
                {
                    showScannerTaskButton =
                            new JButton(lang.get(MacroViewKey.SHOW_SCANNER_TASK_BUTTON));
                    showScannerTaskButton.addActionListener(buttonListener);
                    buttonBox.add(showScannerTaskButton);

                    buttonBox.add(Box.createHorizontalGlue());

                    analyzeScannerTaskButton =
                            new JButton(lang.get(MacroViewKey.ANALYZE_SCANNER_TASK_BUTTON));
                    analyzeScannerTaskButton.addActionListener(buttonListener);
                    buttonBox.add(analyzeScannerTaskButton);
                }
                taskPanel.add(buttonBox, BorderLayout.PAGE_END);
            }
            tabbedPane.addTab(lang.get(MacroViewKey.TAB_SCANNER_TASK), taskPanel);

            JPanel resultPanel = new JPanel(new BorderLayout());
            {
                // scanner table panel
                JScrollPane scannerTableScrollPane;
                {
                    scannerResultTable = new JTable();
                    scannerResultTable.getTableHeader().setReorderingAllowed(false);
                    scannerResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    scannerResultTable.setBackground(Color.WHITE);
                    scannerResultTable.setSelectionBackground(TABLE_SELECTED_BACKGROUND_COLOR);
                    scannerResultTable.setSelectionForeground(TABLE_SELECTED_FOREGROUND_COLOR);
                    scannerResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                    scannerResultTable.setDefaultRenderer(Pattern.class,
                            new DefaultTableCellRenderer() {
                                @Serial
                                private static final long serialVersionUID = -2356333891819256448L;

                                @Override
                                public Component getTableCellRendererComponent(JTable table,
                                        Object value, boolean isSelected, boolean hasFocus, int row,
                                        int column) {
                                    String text = ((Pattern) value).getShortName();
                                    return super.getTableCellRendererComponent(table, text,
                                            isSelected, hasFocus, row, column);
                                }
                            });

                    scannerTableScrollPane = new JScrollPane(scannerResultTable);
                    scannerTableScrollPane.getViewport().setBackground(Color.WHITE);
                }
                resultPanel.add(scannerTableScrollPane, BorderLayout.CENTER);

                // button box
                Box buttonBox = Box.createHorizontalBox();
                {
                    refreshScannerResultButton =
                            new JButton(lang.get(MacroViewKey.REFRESH_SCANNER_RESULT_BUTTON));
                    refreshScannerResultButton.addActionListener(buttonListener);
                    buttonBox.add(refreshScannerResultButton);

                    buttonBox.add(Box.createHorizontalGlue());

                    selectAllRecordButton = new JButton(lang.get(MacroViewKey.SELECT_ALL_BUTTON));
                    selectAllRecordButton.addActionListener(buttonListener);
                    buttonBox.add(selectAllRecordButton);

                    unselectAllRecordButton =
                            new JButton(lang.get(MacroViewKey.UNSELECT_ALL_BUTTON));
                    unselectAllRecordButton.addActionListener(buttonListener);
                    buttonBox.add(unselectAllRecordButton);

                    buttonBox.add(Box.createHorizontalGlue());

                    uploadRecordButton = new JButton(lang.get(MacroViewKey.UPLOAD_RECORD_BUTTON));
                    uploadRecordButton.addActionListener(buttonListener);
                    buttonBox.add(uploadRecordButton);
                }
                resultPanel.add(buttonBox, BorderLayout.PAGE_END);
            }
            tabbedPane.addTab(lang.get(MacroViewKey.TAB_SCANNER_RESULT), resultPanel);

            JScrollPane settingScrollPane = new JScrollPane();
            settingScrollPane.getVerticalScrollBar().setUnitIncrement(16);
            {
                String COLON = ": ";

                // base panel
                JPanel basePanel = new JPanel(new BorderLayout());
                settingScrollPane.setViewportView(basePanel);

                // layout panel
                JPanel layoutPanel = new JPanel();
                EasyGroupLayout layout = new EasyGroupLayout(layoutPanel);
                layout.setAutoCreateContainerGaps(true);
                layout.setAutoCreateGaps(true);
                layoutPanel.setLayout(layout);
                basePanel.add(layoutPanel, BorderLayout.PAGE_START);

                Table<Integer, Integer, Component> components = TreeBasedTable.create();
                {
                    int row = 0;
                    int column = 0;

                    components.put(row, column++, new JLabel(
                            lang.get(MacroViewKey.SETTING_SCANNER_ACCOUNT_FILE) + COLON));

                    accountFileTextField = new JTextField();
                    accountFileTextField.setBackground(Color.WHITE);
                    accountFileTextField.setEditable(false);
                    components.put(row, column++, accountFileTextField);

                    selectAccountFileButton =
                            new JButton(lang.get(MacroViewKey.SETTING_SCANNER_SELECT));
                    selectAccountFileButton.addActionListener(buttonListener);
                    components.put(row, column, selectAccountFileButton);
                }
                {
                    int row = 1;
                    int column = 0;

                    components.put(row, column++, new JLabel(
                            lang.get(MacroViewKey.SETTING_SCANNER_CACHE_DIRECTORY) + COLON));

                    cacheDirTextField = new JTextField();
                    cacheDirTextField.setBackground(Color.WHITE);
                    cacheDirTextField.setEditable(false);
                    components.put(row, column++, cacheDirTextField);

                    selectCacheDirectoryButton =
                            new JButton(lang.get(MacroViewKey.SETTING_SCANNER_SELECT));
                    selectCacheDirectoryButton.addActionListener(buttonListener);
                    components.put(row, column, selectCacheDirectoryButton);
                }
                {
                    int row = 2;
                    int column = 0;

                    recordUploadDelaySlider = new SliderSet();
                    recordUploadDelaySlider.setDefault(RECORD_UPLOAD_DELAY);
                    recordUploadDelaySlider.setLimitMax(1000);
                    recordUploadDelaySlider.setLimitMin(10);

                    recordUploadDelaySlider.label.setText(
                            lang.get(MacroViewKey.SETTING_SCANNER_RECORD_UPLOAD_DELAY) + COLON);
                    components.put(row, column++, recordUploadDelaySlider.label);

                    recordUploadDelaySlider.slider.setMajorTickSpacing(40);
                    recordUploadDelaySlider.slider.setMaximum(200);
                    recordUploadDelaySlider.slider.setMinimum(0);
                    recordUploadDelaySlider.slider.setMinorTickSpacing(10);
                    recordUploadDelaySlider.slider.setPaintLabels(true);
                    recordUploadDelaySlider.slider.setPaintTicks(true);
                    components.put(row, column++, recordUploadDelaySlider.slider);

                    recordUploadDelaySlider.textField.setColumns(8);
                    ComponentSize.preventExpand(recordUploadDelaySlider.textField);
                    components.put(row, column, recordUploadDelaySlider.textField);
                }
                {
                    int row = 3;
                    int column = 0;

                    scannerCaptureDelay = new SliderSet();
                    scannerCaptureDelay.setDefault(SCANNER_CAPTURE_DELAY);
                    scannerCaptureDelay.setLimitMax(1000);
                    scannerCaptureDelay.setLimitMin(0);

                    scannerCaptureDelay.label.setText(
                            lang.get(MacroViewKey.SETTING_CAPTURE_DELAY) + COLON);
                    components.put(row, column++, scannerCaptureDelay.label);

                    scannerCaptureDelay.slider.setMajorTickSpacing(100);
                    scannerCaptureDelay.slider.setMaximum(500);
                    scannerCaptureDelay.slider.setMinimum(0);
                    scannerCaptureDelay.slider.setMinorTickSpacing(50);
                    scannerCaptureDelay.slider.setPaintLabels(true);
                    scannerCaptureDelay.slider.setPaintTicks(true);
                    components.put(row, column++, scannerCaptureDelay.slider);

                    scannerCaptureDelay.textField.setColumns(8);
                    ComponentSize.preventExpand(scannerCaptureDelay.textField);
                    components.put(row, column, scannerCaptureDelay.textField);
                }
                {
                    int row = 4;
                    int column = 0;

                    scannerKeyInputDuration = new SliderSet();
                    scannerKeyInputDuration.setDefault(KEY_INPUT_DURATION);
                    scannerKeyInputDuration.setLimitMax(100);
                    scannerKeyInputDuration.setLimitMin(0);

                    scannerKeyInputDuration.label.setText(
                            lang.get(MacroViewKey.SETTING_KEY_INPUT_DURATION) + COLON);
                    components.put(row, column++, scannerKeyInputDuration.label);

                    scannerKeyInputDuration.slider.setMajorTickSpacing(20);
                    scannerKeyInputDuration.slider.setMaximum(100);
                    scannerKeyInputDuration.slider.setMinimum(0);
                    scannerKeyInputDuration.slider.setMinorTickSpacing(10);
                    scannerKeyInputDuration.slider.setPaintLabels(true);
                    scannerKeyInputDuration.slider.setPaintTicks(true);
                    components.put(row, column++, scannerKeyInputDuration.slider);

                    scannerKeyInputDuration.textField.setColumns(8);
                    ComponentSize.preventExpand(scannerKeyInputDuration.textField);
                    components.put(row, column, scannerKeyInputDuration.textField);
                }

                layout.setContents(components);
            }
            tabbedPane.addTab(lang.get(MacroViewKey.TAB_SCANNER_SETTING), settingScrollPane);
        }
        panel.add(tabbedPane, BorderLayout.CENTER);

        // dlc panel
        JPanel dlcPanel = new JPanel(new BorderLayout());
        {
            dlcCheckboxScrollPane = new JScrollPane();
            dlcCheckboxScrollPane.getVerticalScrollBar().setUnitIncrement(16);
            dlcCheckboxScrollPane.setBorder(
                    BorderFactory.createTitledBorder(lang.get(MacroViewKey.DLC_CHECKBOX_TITLE)));
            dlcPanel.add(dlcCheckboxScrollPane, BorderLayout.CENTER);

            // button box
            Box buttonBox = Box.createVerticalBox();
            {
                Box box = Box.createHorizontalBox();
                {
                    selectAllDlcButton = new JButton(lang.get(MacroViewKey.SELECT_ALL_BUTTON));
                    selectAllDlcButton.addActionListener(buttonListener);
                    box.add(selectAllDlcButton);

                    unselectAllDlcButton = new JButton(lang.get(MacroViewKey.UNSELECT_ALL_BUTTON));
                    unselectAllDlcButton.addActionListener(buttonListener);
                    box.add(unselectAllDlcButton);
                }
                buttonBox.add(box);

                showExpectedButton = new JButton(lang.get(MacroViewKey.SHOW_EXPECTED_BUTTON));
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
        String COLON = ": ";

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        {
            // base panel
            JPanel basePanel = new JPanel(new BorderLayout());
            scrollPane.setViewportView(basePanel);

            // layout panel
            JPanel layoutPanel = new JPanel();
            EasyGroupLayout layout = new EasyGroupLayout(layoutPanel);
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
            layoutPanel.setLayout(layout);
            basePanel.add(layoutPanel, BorderLayout.PAGE_START);

            Table<Integer, Integer, Component> components = TreeBasedTable.create();
            {
                int row = 0;
                int column = 0;

                components.put(row, column++,
                        new JLabel(lang.get(MacroViewKey.SETTING_MACRO_ANALYZE_KEY) + COLON));

                macroAnalyzeKey = new RadioButtonGroup<>();
                {
                    Box radioBox = Box.createVerticalBox();
                    for (AnalyzeKey key : AnalyzeKey.values()) {
                        JRadioButton button = macroAnalyzeKey.addButton(key);
                        button.setText("[Alt + " + key + "]");
                        radioBox.add(button);
                    }
                    components.put(row, column, radioBox);
                }
            }
            {
                int row = 1;
                int column = 0;

                macroCount = new SliderSet();
                macroCount.setDefault(MACRO_COUNT);
                macroCount.setLimitMax(10000);
                macroCount.setLimitMin(0);

                macroCount.label.setText(lang.get(MacroViewKey.SETTING_MACRO_COUNT) + COLON);
                components.put(row, column++, macroCount.label);

                macroCount.slider.setMajorTickSpacing(200);
                macroCount.slider.setMaximum(1000);
                macroCount.slider.setMinimum(0);
                macroCount.slider.setMinorTickSpacing(50);
                macroCount.slider.setPaintLabels(true);
                macroCount.slider.setPaintTicks(true);
                components.put(row, column++, macroCount.slider);

                macroCount.textField.setColumns(8);
                ComponentSize.preventExpand(macroCount.textField);
                components.put(row, column, macroCount.textField);
            }
            {
                int row = 2;
                int column = 0;

                macroCaptureDelay = new SliderSet();
                macroCaptureDelay.setDefault(MACRO_CAPTURE_DELAY);
                macroCaptureDelay.setLimitMax(2000);
                macroCaptureDelay.setLimitMin(200);

                macroCaptureDelay.label.setText(
                        lang.get(MacroViewKey.SETTING_CAPTURE_DELAY) + COLON);
                components.put(row, column++, macroCaptureDelay.label);

                macroCaptureDelay.slider.setMajorTickSpacing(100);
                macroCaptureDelay.slider.setMaximum(1000);
                macroCaptureDelay.slider.setMinimum(200);
                macroCaptureDelay.slider.setMinorTickSpacing(50);
                macroCaptureDelay.slider.setPaintLabels(true);
                macroCaptureDelay.slider.setPaintTicks(true);
                components.put(row, column++, macroCaptureDelay.slider);

                macroCaptureDelay.textField.setColumns(8);
                ComponentSize.preventExpand(macroCaptureDelay.textField);
                components.put(row, column, macroCaptureDelay.textField);
            }
            {
                int row = 3;
                int column = 0;

                macroCaptureDuration = new SliderSet();
                macroCaptureDuration.setDefault(MACRO_CAPTURE_DURATION);
                macroCaptureDuration.setLimitMax(500);
                macroCaptureDuration.setLimitMin(0);

                macroCaptureDuration.label.setText(
                        lang.get(MacroViewKey.SETTING_MACRO_CAPTURE_DURATION) + COLON);
                components.put(row, column++, macroCaptureDuration.label);

                macroCaptureDuration.slider.setMajorTickSpacing(50);
                macroCaptureDuration.slider.setMaximum(200);
                macroCaptureDuration.slider.setMinimum(0);
                macroCaptureDuration.slider.setMinorTickSpacing(10);
                macroCaptureDuration.slider.setPaintLabels(true);
                macroCaptureDuration.slider.setPaintTicks(true);
                components.put(row, column++, macroCaptureDuration.slider);

                macroCaptureDuration.textField.setColumns(8);
                ComponentSize.preventExpand(macroCaptureDuration.textField);
                components.put(row, column, macroCaptureDuration.textField);
            }
            {
                int row = 4;
                int column = 0;

                macroKeyInputDuration = new SliderSet();
                macroKeyInputDuration.setDefault(KEY_INPUT_DURATION);
                macroKeyInputDuration.setLimitMax(100);
                macroKeyInputDuration.setLimitMin(0);

                macroKeyInputDuration.label.setText(
                        lang.get(MacroViewKey.SETTING_KEY_INPUT_DURATION) + COLON);
                components.put(row, column++, macroKeyInputDuration.label);

                macroKeyInputDuration.slider.setMajorTickSpacing(20);
                macroKeyInputDuration.slider.setMaximum(100);
                macroKeyInputDuration.slider.setMinimum(0);
                macroKeyInputDuration.slider.setMinorTickSpacing(10);
                macroKeyInputDuration.slider.setPaintLabels(true);
                macroKeyInputDuration.slider.setPaintTicks(true);
                components.put(row, column++, macroKeyInputDuration.slider);

                macroKeyInputDuration.textField.setColumns(8);
                ComponentSize.preventExpand(macroKeyInputDuration.textField);
                components.put(row, column, macroKeyInputDuration.textField);
            }

            layout.setContents(components);
        }

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(scrollPane);
        box.add(Box.createHorizontalGlue());

        return box;
    }

    private Component createBottom() {
        JPanel panel = new JPanel(new BorderLayout());

        logTextArea = new JTextArea(LOG_ROWS, 0);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, LOG_FONT_SIZE));
        panel.add(new JScrollPane(logTextArea), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());
        {
            JTextArea textArea = new JTextArea();
            textArea.setBorder(
                    BorderFactory.createTitledBorder(lang.get(MacroViewKey.CONTROL_TITLE)));
            textArea.setEditable(false);
            textArea.setOpaque(false);

            StringBuilder builder = new StringBuilder();
            builder.append("( Ctrl + Home ): ");
            builder.append(lang.get(MacroViewKey.CONTROL_START_SCANNING));
            builder.append(System.lineSeparator());

            builder.append("( Alt + [ ): ");
            builder.append(lang.get(MacroViewKey.CONTROL_START_MACRO_UP));
            builder.append(System.lineSeparator());

            builder.append("( Alt + ] ): ");
            builder.append(lang.get(MacroViewKey.CONTROL_START_MACRO_DOWN));
            builder.append(System.lineSeparator());

            builder.append("( End ): ");
            builder.append(lang.get(MacroViewKey.CONTROL_STOP));
            textArea.setText(builder.toString());

            controlPanel.add(textArea, BorderLayout.CENTER);

            stopCommandButton = new JButton(lang.get(MacroViewKey.CONTROL_STOP));
            stopCommandButton.addActionListener(buttonListener);
            ComponentSize.setPreferredHeight(stopCommandButton, 32);

            controlPanel.add(stopCommandButton, BorderLayout.PAGE_END);
        }
        panel.add(controlPanel, BorderLayout.LINE_END);

        return panel;
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
    public void showRecord(String text, Table<Button, Pattern, String> records) {
        recordViewerTextArea.setText(text);

        recordViewerGridTextFields.cellSet().forEach((cell) -> {
            String value = records.get(cell.getRowKey(), cell.getColumnKey());
            cell.getValue().setText((value != null) ? value : "");
        });
    }

    @Override
    public void setSelectableDlcTabs(List<String> tabs) {
        dlcCheckboxGroup.clear();
        tabs.forEach((x) -> dlcCheckboxGroup.add(x, x));

        Box box = Box.createVerticalBox();
        dlcCheckboxGroup.forEach((key, checkbox) -> box.add(checkbox));

        dlcCheckboxScrollPane.setViewportView(box);
        revalidate();
    }

    @Override
    public void setScannerTaskTableModel(TableModelWithLookup<ColumnKey> model) {
        scannerTaskTable.setModel(model);
        scannerTaskTableModel = model;

        TableColumnLookup<ColumnKey> columnLookup = model.getTableColumnLookup();
        columnLookup.getColumn(scannerTaskTable, ColumnKey.COMPOSER).setPreferredWidth(80);
        columnLookup.getColumn(scannerTaskTable, ColumnKey.DLC).setPreferredWidth(80);
        columnLookup.getColumn(scannerTaskTable, ColumnKey.SONG_NUMBER).setPreferredWidth(60);
        columnLookup.getColumn(scannerTaskTable, ColumnKey.STATUS).setPreferredWidth(160);
        columnLookup.getColumn(scannerTaskTable, ColumnKey.TAB).setPreferredWidth(80);
        columnLookup.getColumn(scannerTaskTable, ColumnKey.TASK_NUMBER).setPreferredWidth(60);
        columnLookup.getColumn(scannerTaskTable, ColumnKey.TITLE).setPreferredWidth(160);

        // Hide two columns, index, and count.
        TableColumnModel columnModel = scannerTaskTable.getColumnModel();
        columnModel.removeColumn(columnLookup.getColumn(scannerTaskTable, ColumnKey.COUNT));
        columnModel.removeColumn(columnLookup.getColumn(scannerTaskTable, ColumnKey.INDEX));
    }

    @Override
    public void setScannerResultTableModel(
            TableModelWithLookup<ScannerResultListViewModels.ColumnKey> model) {
        scannerResultTable.setModel(model);
        scannerResultTableModel = model;

        TableColumnLookup<ScannerResultListViewModels.ColumnKey> columnLookup =
                model.getTableColumnLookup();

        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.BUTTON)
                .setPreferredWidth(40);
        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.COMPOSER)
                .setPreferredWidth(80);
        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.DELTA_RATE)
                .setPreferredWidth(40);
        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.DLC)
                .setPreferredWidth(80);
        columnLookup.getColumn(scannerResultTable,
                ScannerResultListViewModels.ColumnKey.NEW_MAX_COMBO).setPreferredWidth(60);
        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.NEW_RATE)
                .setPreferredWidth(40);
        columnLookup.getColumn(scannerResultTable,
                ScannerResultListViewModels.ColumnKey.OLD_MAX_COMBO).setPreferredWidth(60);
        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.OLD_RATE)
                .setPreferredWidth(40);
        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.PATTERN)
                .setPreferredWidth(40);
        columnLookup.getColumn(scannerResultTable,
                ScannerResultListViewModels.ColumnKey.RESULT_NUMBER).setPreferredWidth(40);
        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.STATUS)
                .setPreferredWidth(160);
        columnLookup.getColumn(scannerResultTable,
                ScannerResultListViewModels.ColumnKey.TASK_NUMBER).setPreferredWidth(40);
        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.TITLE)
                .setPreferredWidth(160);
        columnLookup.getColumn(scannerResultTable, ScannerResultListViewModels.ColumnKey.UPLOAD)
                .setPreferredWidth(80);
    }

    @Override
    public void setScannerResultTableRowSorter(
            TableRowSorter<ScannerResultListViewModel> rowSorter) {
        scannerResultTable.setRowSorter(rowSorter);
    }

    @Override
    public Path getAccountPath() {
        return accountPath;
    }

    @Override
    public void setAccountPath(Path path) {
        accountPath = path;
        accountFileTextField.setText((path != null) ? path.toString() : "");
    }

    @Override
    public Path getCacheDir() {
        return cacheDirPath;
    }

    @Override
    public void setCacheDir(Path path) {
        cacheDirPath = path;
        cacheDirTextField.setText((path != null) ? path.toString() : "");
    }

    @Override
    public Set<String> getSelectedDlcTabs() {
        return dlcCheckboxGroup.getSelected();
    }

    @Override
    public void setSelectedDlcTabs(Set<String> tabs) {
        dlcCheckboxGroup.select(tabs);
    }

    @Override
    public int getRecordUploadDelay() {
        return recordUploadDelaySlider.getValue();
    }

    @Override
    public void setRecordUploadDelay(int value) {
        recordUploadDelaySlider.setValue(value);
    }

    @Override
    public int getScannerCaptureDelay() {
        return scannerCaptureDelay.getValue();
    }

    @Override
    public void setScannerCaptureDelay(int value) {
        scannerCaptureDelay.setValue(value);
    }

    @Override
    public int getScannerKeyInputDuration() {
        return scannerKeyInputDuration.getValue();
    }

    @Override
    public void setScannerKeyInputDuration(int value) {
        scannerKeyInputDuration.setValue(value);
    }

    @Override
    public AnalyzeKey getMacroAnalyzeKey() {
        return macroAnalyzeKey.getSelected();
    }

    @Override
    public void setMacroAnalyzeKey(AnalyzeKey value) {
        macroAnalyzeKey.setSelected(value);
    }

    @Override
    public int getMacroCount() {
        return macroCount.getValue();
    }

    @Override
    public void setMacroCount(int value) {
        macroCount.setValue(value);
    }

    @Override
    public int getMacroCaptureDelay() {
        return macroCaptureDelay.getValue();
    }

    @Override
    public void setMacroCaptureDelay(int value) {
        macroCaptureDelay.setValue(value);
    }

    @Override
    public int getMacroCaptureDuration() {
        return macroCaptureDuration.getValue();
    }

    @Override
    public void setMacroCaptureDuration(int value) {
        macroCaptureDuration.setValue(value);
    }

    @Override
    public int getMacroKeyInputDuration() {
        return macroKeyInputDuration.getValue();
    }

    @Override
    public void setMacroKeyInputDuration(int value) {
        macroKeyInputDuration.setValue(value);
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

        if (source.equals(view.menuExit)) {
            view.presenter.stop();
        } else if (source.equals(view.menuAbout)) {
            JOptionPane.showMessageDialog(view, new AboutPanel());
        } else if (source.equals(view.menuOSL)) {
            view.presenter.openLicenseView(view);
        } else if (source.equals(view.menuLangEng)) {
            view.presenter.changeLanguage(Locale.ENGLISH);
        } else if (source.equals(view.menuLangKor)) {
            view.presenter.changeLanguage(Locale.KOREAN);
        } else {
            view.addLog(source.toString());
        }
    }
}


class MacroViewButtonListener implements ActionListener {
    private final AccountFileChooser accountFileChooser = new AccountFileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private final MacroView view;
    private final Language lang = Language.getInstance();

    public MacroViewButtonListener(MacroView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(view.loadRemoteRecordButton)) {
            String djName =
                    JOptionPane.showInputDialog(view, lang.get(MacroViewKey.DIALOG_DJ_NAME));
            if (djName != null) {
                view.presenter.loadServerRecord(djName);
            }
        } else if (source.equals(view.selectAccountFileButton)) {
            Path path = accountFileChooser.get(view);
            if (path != null) {
                view.accountPath = path;
                view.accountFileTextField.setText(path.toString());
            }
        } else if (source.equals(view.selectCacheDirectoryButton)) {
            Path path = directoryChooser.get(view);
            if (path != null) {
                view.cacheDirPath = path;
                view.cacheDirTextField.setText(path.toString());
            }
        } else if (source.equals(view.showScannerTaskButton)) {
            int index = view.scannerTaskTable.getSelectedRow();
            if (index != -1) {
                Object value = view.scannerTaskTable.getValueAt(index, 0);
                if (value instanceof Integer taskNumber) {
                    view.presenter.showScannerTask(view, taskNumber);
                }
            }
        } else if (source.equals(view.analyzeScannerTaskButton)) {
            view.presenter.analyzeScannerTask();
        } else if (source.equals(view.refreshScannerResultButton)) {
            view.presenter.refreshScannerResult();
        } else if (source.equals(view.selectAllRecordButton)) {
            int count = view.scannerResultTable.getRowCount();
            int index = view.scannerResultTableModel.getTableColumnLookup()
                    .getIndexInView(view.scannerResultTable,
                            ScannerResultListViewModels.ColumnKey.UPLOAD);
            for (int i = 0; i < count; ++i) {
                view.scannerResultTable.setValueAt(true, i, index);
            }
        } else if (source.equals(view.unselectAllRecordButton)) {
            int count = view.scannerResultTable.getRowCount();
            int index = view.scannerResultTableModel.getTableColumnLookup()
                    .getIndexInView(view.scannerResultTable,
                            ScannerResultListViewModels.ColumnKey.UPLOAD);
            for (int i = 0; i < count; ++i) {
                view.scannerResultTable.setValueAt(false, i, index);
            }
        } else if (source.equals(view.uploadRecordButton)) {
            Box box = Box.createVerticalBox();

            JLabel message0 = new JLabel(lang.get(MacroViewKey.DIALOG_UPLOAD_MESSAGE0));
            message0.setForeground(Color.RED);
            box.add(message0);
            box.add(Box.createVerticalStrut(10));

            box.add(new JLabel(lang.get(MacroViewKey.DIALOG_UPLOAD_MESSAGE1)));
            box.add(new JLabel(lang.get(MacroViewKey.DIALOG_UPLOAD_MESSAGE2)));
            box.add(Box.createVerticalStrut(10));

            JCheckBox checkBox = new JCheckBox(lang.get(MacroViewKey.DIALOG_UPLOAD_CHECKBOX));
            box.add(checkBox);

            int selected = JOptionPane.showConfirmDialog(view, box,
                    lang.get(MacroViewKey.DIALOG_UPLOAD_TITLE), JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (selected == JOptionPane.YES_OPTION) {
                if (!checkBox.isSelected()) {
                    view.addLog(lang.get(MacroViewKey.DIALOG_UPLOAD_CANCELED));
                    return;
                }

                view.presenter.uploadRecord(view.accountPath);
            }
        } else if (source.equals(view.selectAllDlcButton)) {
            view.dlcCheckboxGroup.selectAll();
        } else if (source.equals(view.unselectAllDlcButton)) {
            view.dlcCheckboxGroup.unselectAll();
        } else if (source.equals(view.showExpectedButton)) {
            view.presenter.openExpected(view);
        } else if (source.equals(view.stopCommandButton)) {
            view.presenter.stopCommand();
        } else {
            view.addLog(source.toString());
        }
    }
}


class AccountFileChooser extends JFileChooser {
    @Serial
    private static final long serialVersionUID = 1407963872565375369L;

    private static final Path CURRENT_DIRECTORY = Path.of("").toAbsolutePath();

    public AccountFileChooser() {
        setCurrentDirectory(CURRENT_DIRECTORY.toFile());
        setFileFilter(new FileNameExtensionFilter("Account file (*.txt)", "txt"));
        setMultiSelectionEnabled(false);
    }

    public Path get(JFrame frame) {
        if (showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        Path path = getSelectedFile().toPath();
        return path.startsWith(CURRENT_DIRECTORY) ? CURRENT_DIRECTORY.relativize(path) : path;
    }
}


class DirectoryChooser extends JFileChooser {
    @Serial
    private static final long serialVersionUID = -1770798834986186727L;

    private static final Path CURRENT_DIRECTORY = Path.of("").toAbsolutePath();

    public DirectoryChooser() {
        setCurrentDirectory(CURRENT_DIRECTORY.toFile());
        setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        setMultiSelectionEnabled(false);
    }

    public Path get(JFrame frame) {
        if (showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        Path path = getSelectedFile().toPath();
        return path.startsWith(CURRENT_DIRECTORY) ? CURRENT_DIRECTORY.relativize(path) : path;
    }
}


class AboutPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = -7443336903636741728L;

    private static final Language lang = Language.getInstance();
    private static final String GITHUB_URL = "https://github.com/johypark97/VArchiveMacro";

    public AboutPanel() {
        super(new BorderLayout());

        JLabel title = new JLabel("VArchive Macro");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        add(title, BorderLayout.PAGE_START);

        JPanel center = new JPanel();
        {
            EasyGroupLayout layout = new EasyGroupLayout(center);
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
            center.setLayout(layout);

            Table<Integer, Integer, Component> components = TreeBasedTable.create();
            {
                int row = 0;
                int column = 0;
                components.put(row, column++, new JLabel(lang.get(MacroViewKey.ABOUT_VERSION)));
                components.put(row, column, new JLabel(BuildInfo.version));
            }
            {
                int row = 1;
                int column = 0;
                components.put(row, column++, new JLabel(lang.get(MacroViewKey.ABOUT_DATE)));
                components.put(row, column, new JLabel(BuildInfo.date));
            }
            {
                int row = 2;
                int column = 0;
                components.put(row, column++, new JLabel(lang.get(MacroViewKey.ABOUT_SOURCE_CODE)));

                UrlLabel url = new UrlLabel();
                url.setUrl(GITHUB_URL);
                components.put(row, column, url);
            }

            layout.setContents(components);
        }
        add(center, BorderLayout.CENTER);
    }
}


class EasyGroupLayout extends GroupLayout {
    public EasyGroupLayout(Container host) {
        super(host);
    }

    public void setContents(Table<Integer, Integer, Component> contents) {
        SequentialGroup hGroup = createSequentialGroup();
        contents.columnMap().forEach((column, columnMap) -> {
            ParallelGroup group;
            if (column == 0) {
                group = createParallelGroup(Alignment.TRAILING);
            } else {
                group = createParallelGroup();
            }

            columnMap.forEach((row, x) -> group.addComponent(x));
            hGroup.addGroup(group);
        });
        setHorizontalGroup(hGroup);

        SequentialGroup vGroup = createSequentialGroup();
        contents.rowMap().forEach((row, rowMap) -> {
            ParallelGroup group = createParallelGroup(Alignment.CENTER);
            rowMap.forEach((column, x) -> group.addComponent(x));
            vGroup.addGroup(group);
        });
        setVerticalGroup(vGroup);
    }
}
