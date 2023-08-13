package com.github.johypark97.varchivemacro.dbmanager.gui.view;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.Presenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.View;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.CacheGeneratorConfig;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.GroundTruthGeneratorConfig;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.LiveTesterConfig;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.OcrTesterConfig;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.OcrTesterViewModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.OcrTesterViewModelColumn.ColumnKey;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.SongViewModel;
import com.github.johypark97.varchivemacro.lib.common.gui.component.GrowBoxCreator;
import com.github.johypark97.varchivemacro.lib.common.gui.component.SliderSet;
import com.github.johypark97.varchivemacro.lib.common.gui.util.ComponentSize;
import com.github.johypark97.varchivemacro.lib.common.gui.util.TableUtil;
import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableColumnLookup;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serial;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;

public class DbManagerView extends JFrame implements View, WindowListener {
    @Serial
    private static final long serialVersionUID = 1539051679420322263L;

    private static final String SELECT_TEXT = "Select";
    private static final String TITLE = "Database Manager";
    private static final int WINDOW_HEIGHT = 600;
    private static final int WINDOW_WIDTH = 800;

    private static final Color TABLE_SELECTED_BACKGROUND_COLOR = new Color(0xE0E0E0);
    private static final Color TABLE_SELECTED_FOREGROUND_COLOR = Color.BLACK;

    // presenter
    protected transient Presenter presenter;

    // components
    protected JMenuItem menuItemExit;

    protected JButton songsFileLoadButton;
    protected JButton songsFileSelectButton;
    protected JTextField databaseDirectoryTextField;

    private JTable songsTable;
    protected JButton songsFilterResetButton;
    protected JComboBox<String> songsFilterColumnComboBox;
    protected JTextField songsFilterTextField;

    private JTextArea validatorTextArea;
    protected JButton validateButton;

    private JTextArea remoteCheckerTextArea;
    protected JButton checkRemoteButton;

    private transient Path cacheGeneratorDir;
    private transient SliderSet captureCountSet;
    private transient SliderSet captureDelaySliderSet;
    private transient SliderSet continuousCaptureDelaySet;
    private transient SliderSet inputDurationSliderSet;

    private transient Path groundTruthGeneratorInputDir;
    private transient Path groundTruthGeneratorOutputDir;
    protected JButton generateGroundTruthButton;

    private JTable ocrTesterTable;
    private transient Path ocrTesterCacheDirPath;
    protected JButton ocrTesterRunTestButton;
    protected JButton ocrTesterSelectTrainedDataButton;
    protected JTextField ocrTesterTrainedDataTextField;
    protected transient Path ocrTesterTrainedDataPath;

    protected JButton liveTesterCloseViewButton;
    protected JButton liveTesterOpenViewButton;
    protected JButton liveTesterSelectTrainedDataButton;
    protected JTextField liveTesterTrainedDataDirectoryTextField;
    protected JTextField liveTesterTrainedDataLanguageTextField;
    protected transient Path liveTesterTrainedDataPath;

    // event listeners
    private transient final ActionListener buttonListener = new DbManagerViewButtonListener(this);
    private transient final ActionListener menuListener = new DbManagerViewMenuListener(this);
    private transient final DocumentListener documentListener =
            new DbManagerViewDocumentListener(this);

    // view models
    public OcrTesterViewModel ocrTesterTableViewModel;
    public SongViewModel songsTableViewModel;

    public DbManagerView() {
        super(TITLE);

        setFrameOption();
        setContentPanel();
        setContent();

        addWindowListener(this);
    }

    protected void updateSongFilter() {
        Object value = songsFilterColumnComboBox.getSelectedItem();
        if (value == null) {
            return;
        }

        String columnName = (String) value;
        String pattern = songsFilterTextField.getText();
        songsTableViewModel.setFilter(columnName, pattern);
    }

    private void setFrameOption() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(true);
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        setLocationRelativeTo(null);
    }

    private void setContentPanel() {
        setContentPane(new JPanel(new BorderLayout()));
    }

    private void setContent() {
        add(createMenu(), BorderLayout.PAGE_START);
        add(createCenter(), BorderLayout.CENTER);
    }

    private Component createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // menu file
        JMenu menuFile = new JMenu("File");

        menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(menuListener);
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        return menuBar;
    }

    private Component createCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createHeader(), BorderLayout.PAGE_START);

        JTabbedPane tabbedPane = new JTabbedPane();
        {
            tabbedPane.addTab("Viewer", createTabViewer());
            tabbedPane.addTab("Validator", createTabValidator());
            tabbedPane.addTab("Remote Checker", createTabRemoteChecker());

            JTabbedPane titleOcrTab = new JTabbedPane();
            {
                titleOcrTab.addTab("Cache Generator", createTabCacheGenerator());
                titleOcrTab.addTab("Ground Truth Generator", createTabGroundTruthGenerator());
                titleOcrTab.addTab("Ocr Tester", createTabOcrTester());
                titleOcrTab.addTab("Live Tester", createTabLiveTester());
            }
            tabbedPane.addTab("Title OCR Tools", titleOcrTab);
        }
        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private Component createHeader() {
        Box box = Box.createHorizontalBox();

        // label
        box.add(new JLabel("Database directory : "));

        // text field
        databaseDirectoryTextField = new JTextField();
        box.add(databaseDirectoryTextField);

        // button
        songsFileSelectButton = new JButton(SELECT_TEXT);
        songsFileSelectButton.addActionListener(buttonListener);
        box.add(songsFileSelectButton);

        songsFileLoadButton = new JButton("load");
        songsFileLoadButton.addActionListener(buttonListener);
        box.add(songsFileLoadButton);

        return box;
    }

    private Component createTabViewer() {
        JPanel panel = new JPanel(new BorderLayout());

        // tool box
        Box toolBox = Box.createHorizontalBox();
        toolBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        {
            toolBox.add(new JLabel("Regex Filter : "));

            songsFilterTextField = new JTextField();
            songsFilterTextField.getDocument().addDocumentListener(documentListener);
            toolBox.add(songsFilterTextField);

            songsFilterColumnComboBox = new JComboBox<>();
            songsFilterColumnComboBox.addActionListener(buttonListener);
            toolBox.add(songsFilterColumnComboBox);

            songsFilterResetButton = new JButton("reset");
            songsFilterResetButton.addActionListener(buttonListener);
            toolBox.add(songsFilterResetButton);
        }
        panel.add(toolBox, BorderLayout.PAGE_START);

        // table
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        {
            songsTable = new JTable() {
                @Serial
                private static final long serialVersionUID = -1675524704391374766L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
                }

                @Override
                public String getToolTipText(MouseEvent event) {
                    Point p = event.getPoint();
                    int rowIndex = rowAtPoint(p);
                    int colIndex = columnAtPoint(p);

                    try {
                        return getValueAt(rowIndex, colIndex).toString();
                    } catch (RuntimeException e) {
                        return null;
                    }
                }
            };
            songsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            songsTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
            songsTable.setRowHeight(32);
            songsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            scrollPane.setViewportView(songsTable);
        }
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private Component createTabValidator() {
        JPanel panel = new JPanel(new BorderLayout());

        // tool box
        Box toolBox = Box.createHorizontalBox();
        toolBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        {
            toolBox.add(Box.createHorizontalGlue());

            validateButton = new JButton("validate");
            validateButton.addActionListener(buttonListener);
            toolBox.add(validateButton);

            toolBox.add(Box.createHorizontalGlue());
        }
        panel.add(toolBox, BorderLayout.PAGE_START);

        // text area
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        {
            validatorTextArea = new JTextArea();
            validatorTextArea.setEditable(false);

            scrollPane.setViewportView(validatorTextArea);
        }
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private Component createTabRemoteChecker() {
        JPanel panel = new JPanel(new BorderLayout());

        // tool box
        Box toolBox = Box.createHorizontalBox();
        toolBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        {
            toolBox.add(Box.createHorizontalGlue());

            checkRemoteButton = new JButton("check");
            checkRemoteButton.addActionListener(buttonListener);
            toolBox.add(checkRemoteButton);

            toolBox.add(Box.createHorizontalGlue());
        }
        panel.add(toolBox, BorderLayout.PAGE_START);

        // text area
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        {
            remoteCheckerTextArea = new JTextArea();
            remoteCheckerTextArea.setEditable(false);

            scrollPane.setViewportView(remoteCheckerTextArea);
        }
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private Component createTabCacheGenerator() {
        GrowBoxCreator growBoxCreator = new GrowBoxCreator();

        Box pathBox = Box.createHorizontalBox();
        {
            pathBox.add(new JLabel("CacheDir : "));

            cacheGeneratorDir = new CacheGeneratorConfig().cacheDir;

            JTextField cacheGeneratorDirTextField = new JTextField(cacheGeneratorDir.toString());
            cacheGeneratorDirTextField.setBackground(Color.WHITE);
            cacheGeneratorDirTextField.setEditable(false);
            pathBox.add(cacheGeneratorDirTextField);

            JButton cacheGeneratorDirSelectButton = new JButton(SELECT_TEXT);
            cacheGeneratorDirSelectButton.setEnabled(false);
            pathBox.add(cacheGeneratorDirSelectButton);
        }
        growBoxCreator.add(pathBox);

        Box countBox = Box.createHorizontalBox();
        {
            captureCountSet = new SliderSet();
            captureCountSet.setDefault(new CacheGeneratorConfig().count);
            captureCountSet.setLimitMax(100);
            captureCountSet.setLimitMin(1);

            captureCountSet.label.setText("CaptureCount : ");
            countBox.add(captureCountSet.label);

            captureCountSet.slider.setMajorTickSpacing(10);
            captureCountSet.slider.setMaximum(50);
            captureCountSet.slider.setMinimum(0);
            captureCountSet.slider.setMinorTickSpacing(5);
            captureCountSet.slider.setPaintLabels(true);
            captureCountSet.slider.setPaintTicks(true);
            captureCountSet.setValue(new CacheGeneratorConfig().count);
            countBox.add(captureCountSet.slider);

            captureCountSet.textField.setColumns(8);
            ComponentSize.preventExpand(captureCountSet.textField);
            countBox.add(captureCountSet.textField);
        }
        growBoxCreator.add(countBox);

        Box captureDelayBox = Box.createHorizontalBox();
        {
            captureDelaySliderSet = new SliderSet();
            captureDelaySliderSet.setDefault(new CacheGeneratorConfig().captureDelay);
            captureDelaySliderSet.setLimitMax(1000);
            captureDelaySliderSet.setLimitMin(0);

            captureDelaySliderSet.label.setText("CaptureDelay : ");
            captureDelayBox.add(captureDelaySliderSet.label);

            captureDelaySliderSet.slider.setMajorTickSpacing(100);
            captureDelaySliderSet.slider.setMaximum(500);
            captureDelaySliderSet.slider.setMinimum(0);
            captureDelaySliderSet.slider.setMinorTickSpacing(50);
            captureDelaySliderSet.slider.setPaintLabels(true);
            captureDelaySliderSet.slider.setPaintTicks(true);
            captureDelaySliderSet.setValue(new CacheGeneratorConfig().captureDelay);
            captureDelayBox.add(captureDelaySliderSet.slider);

            captureDelaySliderSet.textField.setColumns(8);
            ComponentSize.preventExpand(captureDelaySliderSet.textField);
            captureDelayBox.add(captureDelaySliderSet.textField);
        }
        growBoxCreator.add(captureDelayBox);

        Box continuousCaptureDelayBox = Box.createHorizontalBox();
        {
            continuousCaptureDelaySet = new SliderSet();
            continuousCaptureDelaySet.setDefault(new CacheGeneratorConfig().continuousCaptureDelay);
            continuousCaptureDelaySet.setLimitMax(1000);
            continuousCaptureDelaySet.setLimitMin(0);

            continuousCaptureDelaySet.label.setText("ContinuousCaptureDelay : ");
            continuousCaptureDelayBox.add(continuousCaptureDelaySet.label);

            continuousCaptureDelaySet.slider.setMajorTickSpacing(100);
            continuousCaptureDelaySet.slider.setMaximum(500);
            continuousCaptureDelaySet.slider.setMinimum(0);
            continuousCaptureDelaySet.slider.setMinorTickSpacing(50);
            continuousCaptureDelaySet.slider.setPaintLabels(true);
            continuousCaptureDelaySet.slider.setPaintTicks(true);
            continuousCaptureDelaySet.setValue(new CacheGeneratorConfig().continuousCaptureDelay);
            continuousCaptureDelayBox.add(continuousCaptureDelaySet.slider);

            continuousCaptureDelaySet.textField.setColumns(8);
            ComponentSize.preventExpand(continuousCaptureDelaySet.textField);
            continuousCaptureDelayBox.add(continuousCaptureDelaySet.textField);
        }
        growBoxCreator.add(continuousCaptureDelayBox);

        Box inputDurationBox = Box.createHorizontalBox();
        {
            inputDurationSliderSet = new SliderSet();
            inputDurationSliderSet.setDefault(new CacheGeneratorConfig().inputDuration);
            inputDurationSliderSet.setLimitMax(100);
            inputDurationSliderSet.setLimitMin(0);

            inputDurationSliderSet.label.setText("InputDuration : ");
            inputDurationBox.add(inputDurationSliderSet.label);

            inputDurationSliderSet.slider.setMajorTickSpacing(20);
            inputDurationSliderSet.slider.setMaximum(100);
            inputDurationSliderSet.slider.setMinimum(0);
            inputDurationSliderSet.slider.setMinorTickSpacing(10);
            inputDurationSliderSet.slider.setPaintLabels(true);
            inputDurationSliderSet.slider.setPaintTicks(true);
            inputDurationSliderSet.setValue(new CacheGeneratorConfig().inputDuration);
            inputDurationBox.add(inputDurationSliderSet.slider);

            inputDurationSliderSet.textField.setColumns(8);
            ComponentSize.preventExpand(inputDurationSliderSet.textField);
            inputDurationBox.add(inputDurationSliderSet.textField);
        }
        growBoxCreator.add(inputDurationBox);

        return growBoxCreator.create();
    }

    private Component createTabGroundTruthGenerator() {
        GrowBoxCreator growBoxCreator = new GrowBoxCreator();

        Box inputDirBox = Box.createHorizontalBox();
        {
            inputDirBox.add(new JLabel("input : "));

            groundTruthGeneratorInputDir = new GroundTruthGeneratorConfig().inputDir;

            JTextField textField = new JTextField(groundTruthGeneratorInputDir.toString());
            textField.setBackground(Color.WHITE);
            textField.setEditable(false);
            inputDirBox.add(textField);

            JButton button = new JButton(SELECT_TEXT);
            button.setEnabled(false);
            inputDirBox.add(button);
        }
        growBoxCreator.add(inputDirBox);

        Box outputDirBox = Box.createHorizontalBox();
        {
            outputDirBox.add(new JLabel("output : "));

            groundTruthGeneratorOutputDir = new GroundTruthGeneratorConfig().outputDir;

            JTextField textField = new JTextField(groundTruthGeneratorOutputDir.toString());
            textField.setBackground(Color.WHITE);
            textField.setEditable(false);
            outputDirBox.add(textField);

            JButton button = new JButton(SELECT_TEXT);
            button.setEnabled(false);
            outputDirBox.add(button);
        }
        growBoxCreator.add(outputDirBox);

        Box buttonBox = Box.createHorizontalBox();
        {
            buttonBox.add(Box.createHorizontalGlue());

            generateGroundTruthButton = new JButton("generate");
            generateGroundTruthButton.addActionListener(buttonListener);
            buttonBox.add(generateGroundTruthButton);

            buttonBox.add(Box.createHorizontalGlue());
        }
        growBoxCreator.add(buttonBox);

        return growBoxCreator.create();
    }

    private Component createTabOcrTester() {
        JPanel panel = new JPanel(new BorderLayout());

        Box toolBox = Box.createVerticalBox();
        {
            Box dataBox = Box.createHorizontalBox();
            {
                dataBox.add(new JLabel("trainedData : "));

                ocrTesterTrainedDataPath = new OcrTesterConfig().trainedDataPath;

                ocrTesterTrainedDataTextField = new JTextField(ocrTesterTrainedDataPath.toString());
                ocrTesterTrainedDataTextField.setBackground(Color.WHITE);
                ocrTesterTrainedDataTextField.setEditable(false);
                dataBox.add(ocrTesterTrainedDataTextField);

                ocrTesterSelectTrainedDataButton = new JButton(SELECT_TEXT);
                ocrTesterSelectTrainedDataButton.addActionListener(buttonListener);
                dataBox.add(ocrTesterSelectTrainedDataButton);
            }
            toolBox.add(dataBox);

            Box cacheBox = Box.createHorizontalBox();
            {
                cacheBox.add(new JLabel("cacheDir : "));

                ocrTesterCacheDirPath = new OcrTesterConfig().cachePath;

                JTextField textField = new JTextField(ocrTesterCacheDirPath.toString());
                textField.setBackground(Color.WHITE);
                textField.setEditable(false);
                cacheBox.add(textField);

                JButton button = new JButton(SELECT_TEXT);
                button.setEnabled(false);
                cacheBox.add(button);
            }
            toolBox.add(cacheBox);

            Box buttonBox = Box.createHorizontalBox();
            {
                buttonBox.add(Box.createHorizontalGlue());

                ocrTesterRunTestButton = new JButton("run test");
                ocrTesterRunTestButton.addActionListener(buttonListener);
                buttonBox.add(ocrTesterRunTestButton);

                buttonBox.add(Box.createHorizontalGlue());
            }
            toolBox.add(buttonBox);
        }
        panel.add(toolBox, BorderLayout.PAGE_START);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Color.WHITE);
        {
            ocrTesterTable = new JTable();
            ocrTesterTable.getTableHeader().setReorderingAllowed(false);
            ocrTesterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            ocrTesterTable.setSelectionBackground(TABLE_SELECTED_BACKGROUND_COLOR);
            ocrTesterTable.setSelectionForeground(TABLE_SELECTED_FOREGROUND_COLOR);
            ocrTesterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            ocrTesterTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    int rowIndex = table.convertRowIndexToModel(row);

                    boolean isMatch = (boolean) table.getModel().getValueAt(rowIndex,
                            ocrTesterTableViewModel.getTableColumnLookup()
                                    .getIndexInView(table, ColumnKey.MATCH));

                    Color backgroundColor;
                    if (isMatch) {
                        backgroundColor = isSelected ? new Color(0x80FF80) : new Color(0xC0FFC0);
                    } else {
                        backgroundColor =
                                isSelected ? table.getSelectionBackground() : table.getBackground();
                    }

                    Component component =
                            super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                    row, column);
                    component.setBackground(backgroundColor);

                    return component;
                }
            });

            scrollPane.setViewportView(ocrTesterTable);
        }
        panel.add(scrollPane);

        return panel;
    }

    private Component createTabLiveTester() {
        GrowBoxCreator growBoxCreator = new GrowBoxCreator();

        Box directoryBox = Box.createHorizontalBox();
        {
            directoryBox.add(new JLabel("trainedData directory : "));

            liveTesterTrainedDataPath = new LiveTesterConfig().trainedDataDirectory;

            liveTesterTrainedDataDirectoryTextField =
                    new JTextField(liveTesterTrainedDataPath.toString());
            liveTesterTrainedDataDirectoryTextField.setBackground(Color.WHITE);
            liveTesterTrainedDataDirectoryTextField.setEditable(false);
            directoryBox.add(liveTesterTrainedDataDirectoryTextField);

            liveTesterSelectTrainedDataButton = new JButton(SELECT_TEXT);
            liveTesterSelectTrainedDataButton.addActionListener(buttonListener);
            directoryBox.add(liveTesterSelectTrainedDataButton);
        }
        growBoxCreator.add(directoryBox);

        Box languageBox = Box.createHorizontalBox();
        {
            languageBox.add(new JLabel("trainedData language : "));

            liveTesterTrainedDataLanguageTextField =
                    new JTextField(new LiveTesterConfig().trainedDataLanguage);
            languageBox.add(liveTesterTrainedDataLanguageTextField);
        }
        growBoxCreator.add(languageBox);

        Box buttonBox = Box.createHorizontalBox();
        {
            buttonBox.add(Box.createHorizontalGlue());

            liveTesterOpenViewButton = new JButton("open");
            liveTesterOpenViewButton.addActionListener(buttonListener);
            buttonBox.add(liveTesterOpenViewButton);

            liveTesterCloseViewButton = new JButton("close");
            liveTesterCloseViewButton.addActionListener(buttonListener);
            buttonBox.add(liveTesterCloseViewButton);

            buttonBox.add(Box.createHorizontalGlue());
        }
        growBoxCreator.add(buttonBox);

        return growBoxCreator.create();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setViewModels(SongViewModel songViewModel, OcrTesterViewModel ocrTesterViewModel) {
        // song view model
        songsTable.setModel(songViewModel);
        songsTableViewModel = songViewModel;

        songsTable.setRowSorter(songsTableViewModel.getRowSorter());
        TableUtil.resizeColumnWidth(songsTable, 40, 400, 10);

        songsFilterColumnComboBox.removeAllItems(); // for reloading
        songsTableViewModel.getFilterableColumnList().forEach(songsFilterColumnComboBox::addItem);

        // ocr tester view model
        ocrTesterTable.setModel(ocrTesterViewModel);
        ocrTesterTableViewModel = ocrTesterViewModel;

        ocrTesterTable.setRowSorter(ocrTesterViewModel.getRowSorter());

        TableColumnLookup<ColumnKey> columnLookup = ocrTesterViewModel.getTableColumnLookup();
        columnLookup.getColumn(ocrTesterTable, ColumnKey.ID).setPreferredWidth(40);
        columnLookup.getColumn(ocrTesterTable, ColumnKey.MATCH).setPreferredWidth(40);
        columnLookup.getColumn(ocrTesterTable, ColumnKey.NORMALIZED_TITLE).setPreferredWidth(160);
        columnLookup.getColumn(ocrTesterTable, ColumnKey.SCANNED_TITLE).setPreferredWidth(160);
        columnLookup.getColumn(ocrTesterTable, ColumnKey.SONG_COMPOSER).setPreferredWidth(80);
        columnLookup.getColumn(ocrTesterTable, ColumnKey.SONG_DLC).setPreferredWidth(80);
        columnLookup.getColumn(ocrTesterTable, ColumnKey.SONG_DLC_TAB).setPreferredWidth(80);
        columnLookup.getColumn(ocrTesterTable, ColumnKey.SONG_TITLE).setPreferredWidth(160);
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
    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setValidatorResultText(String value) {
        validatorTextArea.setText(value);
        validatorTextArea.setCaretPosition(0);
    }

    @Override
    public void setCheckerResultText(String value) {
        remoteCheckerTextArea.setText(value);
        remoteCheckerTextArea.setCaretPosition(0);
    }

    @Override
    public CacheGeneratorConfig getCacheGeneratorConfig() {
        CacheGeneratorConfig config = new CacheGeneratorConfig();

        config.cacheDir = cacheGeneratorDir;
        config.captureDelay = captureDelaySliderSet.getValue();
        config.continuousCaptureDelay = continuousCaptureDelaySet.getValue();
        config.count = captureCountSet.getValue();
        config.inputDuration = inputDurationSliderSet.getValue();

        return config;
    }

    @Override
    public GroundTruthGeneratorConfig getGroundTruthGeneratorConfig() {
        GroundTruthGeneratorConfig config = new GroundTruthGeneratorConfig();

        config.inputDir = groundTruthGeneratorInputDir;
        config.outputDir = groundTruthGeneratorOutputDir;

        return config;
    }

    @Override
    public OcrTesterConfig getOcrTesterConfig() {
        OcrTesterConfig config = new OcrTesterConfig();

        config.cachePath = ocrTesterCacheDirPath;
        config.trainedDataPath = ocrTesterTrainedDataPath;

        return config;
    }

    @Override
    public LiveTesterConfig getLiveTesterConfig() {
        LiveTesterConfig config = new LiveTesterConfig();

        config.trainedDataDirectory = liveTesterTrainedDataPath;
        config.trainedDataLanguage = liveTesterTrainedDataLanguageTextField.getText();

        return config;
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


class DbManagerViewMenuListener implements ActionListener {
    private final DbManagerView view;

    public DbManagerViewMenuListener(DbManagerView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(view.menuItemExit)) {
            view.presenter.stop();
        } else {
            // TODO: Remove println after completing the view.
            System.out.println(source); // NOPMD
        }
    }
}


class DbManagerViewButtonListener implements ActionListener {
    private static final String trainedDataExt = "traineddata";

    private final DbManagerView view;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private final FileChooser fileChooser = new FileChooser();

    private final String trainedDataDesc;

    public DbManagerViewButtonListener(DbManagerView view) {
        this.view = view;

        trainedDataDesc = String.format("Trained data file (*.%s)", trainedDataExt);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(view.songsFileSelectButton)) {
            Path path = directoryChooser.get(view);
            if (path != null) {
                view.databaseDirectoryTextField.setText(path.toString());
            }
        } else if (source.equals(view.songsFileLoadButton)) {
            String path = view.databaseDirectoryTextField.getText();
            view.presenter.loadDatabase(path);
        } else if (source.equals(view.songsFilterResetButton)) {
            view.songsFilterTextField.setText("");
        } else if (source.equals(view.songsFilterColumnComboBox)) {
            view.updateSongFilter();
        } else if (source.equals(view.validateButton)) {
            view.presenter.validateDatabase();
        } else if (source.equals(view.checkRemoteButton)) {
            view.presenter.checkRemote();
        } else if (source.equals(view.generateGroundTruthButton)) {
            view.presenter.generateGroundTruth();
        } else if (source.equals(view.ocrTesterSelectTrainedDataButton)) {
            Path path = fileChooser.get(view, trainedDataDesc, trainedDataExt);
            if (path != null) {
                view.ocrTesterTrainedDataPath = path;
                view.ocrTesterTrainedDataTextField.setText(path.toString());
            }
        } else if (source.equals(view.ocrTesterRunTestButton)) {
            view.presenter.runOcrTest();
        } else if (source.equals(view.liveTesterSelectTrainedDataButton)) {
            Path path = directoryChooser.get(view);
            if (path != null) {
                view.liveTesterTrainedDataPath = path;
                view.liveTesterTrainedDataDirectoryTextField.setText(path.toString());
            }
        } else if (source.equals(view.liveTesterOpenViewButton)) {
            view.presenter.openLiveTester(view);
        } else if (source.equals(view.liveTesterCloseViewButton)) {
            view.presenter.closeLiveTester();
        } else {
            // TODO: Remove println after completing the view.
            System.out.println(source); // NOPMD
        }
    }
}


class DbManagerViewDocumentListener implements DocumentListener {
    private final DbManagerView view;

    public DbManagerViewDocumentListener(DbManagerView view) {
        this.view = view;
    }

    private void update() {
        view.updateSongFilter();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        update();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        update();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
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


class FileChooser extends JFileChooser {
    @Serial
    private static final long serialVersionUID = -1344444442212656120L;

    private static final Path CURRENT_DIRECTORY = Path.of("").toAbsolutePath();

    public FileChooser() {
        setCurrentDirectory(CURRENT_DIRECTORY.toFile());
        setMultiSelectionEnabled(false);
    }

    public Path get(JFrame frame, String description, String... extensions) {
        setFileFilter(new FileNameExtensionFilter(description, extensions));

        if (showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        Path path = getSelectedFile().toPath();
        return path.startsWith(CURRENT_DIRECTORY) ? CURRENT_DIRECTORY.relativize(path) : path;
    }
}
