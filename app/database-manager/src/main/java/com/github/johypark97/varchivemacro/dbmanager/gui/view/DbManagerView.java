package com.github.johypark97.varchivemacro.dbmanager.gui.view;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.Presenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.View;
import com.github.johypark97.varchivemacro.lib.common.gui.util.ComponentSize;
import com.github.johypark97.varchivemacro.lib.common.gui.util.TableUtil;
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
import java.util.List;
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
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class DbManagerView extends JFrame implements View, WindowListener {
    @Serial
    private static final long serialVersionUID = 1539051679420322263L;

    private static final String TITLE = "Database Manager";
    private static final int WINDOW_HEIGHT = 600;
    private static final int WINDOW_WIDTH = 800;

    // presenter
    public transient Presenter presenter;

    // components
    protected JMenuItem menuItemExit;

    protected JButton songsFileLoadButton;
    protected JButton songsFileSelectButton;
    protected JTextField songsFileTextField;

    private JTable songsTable;
    protected JButton songsFilterResetButton;
    protected JComboBox<String> songsFilterColumnComboBox;
    protected JTextField songsFilterTextField;

    private JTextArea checkerResultTextArea;
    protected JButton checkerCheckButton;

    // event listeners
    private transient final ActionListener buttonListener = new DbManagerViewButtonListener(this);
    private transient final ActionListener menuListener = new DbManagerViewMenuListener(this);
    private transient final DocumentListener documentListener =
            new DbManagerViewDocumentListener(this);

    public DbManagerView() {
        super(TITLE);

        setFrameOption();
        setContentPanel();
        setContent();

        addWindowListener(this);
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

        Box box = Box.createVerticalBox();
        box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        box.add(createRow00());
        box.add(createRow01());

        add(box, BorderLayout.CENTER);
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

    private Component createRow00() {
        Box box = Box.createHorizontalBox();

        // label
        box.add(new JLabel("Songs File : "));

        // text field
        songsFileTextField = new JTextField();
        ComponentSize.expandWidthOnly(songsFileTextField);
        box.add(songsFileTextField);

        // button
        songsFileSelectButton = new JButton("select");
        songsFileSelectButton.addActionListener(buttonListener);
        box.add(songsFileSelectButton);

        songsFileLoadButton = new JButton("load");
        songsFileLoadButton.addActionListener(buttonListener);
        box.add(songsFileLoadButton);

        return box;
    }

    private Component createRow01() {
        Box box = Box.createHorizontalBox();

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Viewer", createTabViewer());
        tabbedPane.addTab("Checker", createTabChecker());

        box.add(tabbedPane);

        return box;
    }

    private Component createTabViewer() {
        Box box = Box.createVerticalBox();

        // tool box
        Box toolBox = Box.createHorizontalBox();
        toolBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        toolBox.add(new JLabel("Regex Filter : "));

        songsFilterTextField = new JTextField();
        songsFilterTextField.getDocument().addDocumentListener(documentListener);
        ComponentSize.expandWidthOnly(songsFilterTextField);
        toolBox.add(songsFilterTextField);

        songsFilterColumnComboBox = new JComboBox<>();
        songsFilterColumnComboBox.addActionListener(buttonListener);
        ComponentSize.shrinkWidthToContents(songsFilterColumnComboBox);
        toolBox.add(songsFilterColumnComboBox);

        songsFilterResetButton = new JButton("reset");
        songsFilterResetButton.addActionListener(buttonListener);
        toolBox.add(songsFilterResetButton);

        ComponentSize.shrinkHeightToContents(toolBox);
        box.add(toolBox);

        // table
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

        JScrollPane scrollPane = new JScrollPane(songsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        box.add(scrollPane);

        return box;
    }

    private Component createTabChecker() {
        Box box = Box.createVerticalBox();

        // tool box
        Box toolBox = Box.createHorizontalBox();
        toolBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        checkerCheckButton = new JButton("check");
        checkerCheckButton.addActionListener(buttonListener);
        toolBox.add(checkerCheckButton);

        ComponentSize.shrinkHeightToContents(toolBox);
        box.add(toolBox);

        // result text area
        checkerResultTextArea = new JTextArea();
        checkerResultTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(checkerResultTextArea);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        box.add(scrollPane);

        return box;
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
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public String getSongsFileText() {
        return songsFileTextField.getText();
    }

    @Override
    public void setSongsTableModel(TableModel tableModel) {
        songsTable.setModel(tableModel);
        TableUtil.resizeColumnWidth(songsTable, 40, 400, 10);
    }

    @Override
    public void setSongsTableRowSorter(TableRowSorter<TableModel> tableRowSorter) {
        songsTable.setRowSorter(tableRowSorter);
    }

    @Override
    public void setSongsTableFilterColumnItems(List<String> items) {
        songsFilterColumnComboBox.removeAllItems();
        items.forEach(songsFilterColumnComboBox::addItem);
    }

    @Override
    public String getSongsTableFilterColumn() {
        Object value = songsFilterColumnComboBox.getSelectedItem();
        return (value != null) ? (String) value : "";
    }

    @Override
    public String getSongsTableFilterText() {
        return songsFilterTextField.getText();
    }

    @Override
    public void setCheckerResultText(String value) {
        checkerResultTextArea.setText(value);
    }

    @Override
    public void windowOpened(WindowEvent e) {
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
    private final DbManagerView view;
    private final JsonFileChooser jsonFileChooser = new JsonFileChooser();

    public DbManagerViewButtonListener(DbManagerView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(view.songsFileSelectButton)) {
            Path path = jsonFileChooser.get(view);
            if (path != null) {
                view.songsFileTextField.setText(path.toString());
            }
        } else if (source.equals(view.songsFileLoadButton)) {
            view.presenter.loadSongs();
        } else if (source.equals(view.songsFilterResetButton)) {
            view.songsFilterTextField.setText("");
        } else if (source.equals(view.songsFilterColumnComboBox)) {
            view.presenter.updateFilter();
        } else if (source.equals(view.checkerCheckButton)) {
            view.presenter.checkSongs();
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

    private void update() {
        view.presenter.updateFilter();
    }
}


class JsonFileChooser extends JFileChooser {
    @Serial
    private static final long serialVersionUID = -1770798834986186727L;

    private static final Path CURRENT_DIRECTORY = Path.of(System.getProperty("user.dir"));

    public JsonFileChooser() {
        setCurrentDirectory(CURRENT_DIRECTORY.toFile());
        setFileFilter(new FileNameExtensionFilter("JSON file (*.json)", "json"));
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
