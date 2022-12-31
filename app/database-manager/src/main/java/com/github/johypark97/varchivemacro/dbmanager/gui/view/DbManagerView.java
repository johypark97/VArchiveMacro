package com.github.johypark97.varchivemacro.dbmanager.gui.view;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class DbManagerView extends JFrame implements IDbManager.View {
    @Serial
    private static final long serialVersionUID = 1539051679420322263L;

    private static final String TITLE = "Database Manager";

    private static final int FRAME_HEIGHT = 600;
    private static final int FRAME_WIDTH = 800;

    // presenter
    public transient IDbManager.Presenter presenter;

    // components
    private JMenu menuFile;
    protected JMenuItem menuItemExit;

    protected JButton databaseFileLoadButton;
    protected JButton databaseFileSelectButton;
    protected JTextField databaseFileTextField;

    private JTable viewerTabTable;
    protected JButton viewerTabFilterResetButton;
    protected JComboBox<String> viewerTabFilterComboBox;
    protected JTextField viewerTabFilterTextField;

    // event listeners
    private transient final ActionListener actionListener = new DbManagerViewActionListener(this);
    private transient final ActionListener menuListener = new DbManagerViewMenuListener(this);
    private transient final DocumentListener documentListener =
            new DbManagerViewDocumentListener(this);

    public DbManagerView() {
        setTitle(TITLE);
        setFrameOption();
        setContentPanel();
        setContent();
    }

    private void setFrameOption() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

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
        menuFile = new JMenu("File");

        menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(menuListener);
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        return menuBar;
    }

    private Component createRow00() {
        Box box = Box.createHorizontalBox();

        // label
        box.add(new JLabel("Database : "));

        // text field
        databaseFileTextField = new JTextField();
        ComponentSize.expandWidthOnly(databaseFileTextField);
        box.add(databaseFileTextField);

        // button
        databaseFileSelectButton = new JButton("select");
        databaseFileSelectButton.addActionListener(actionListener);
        box.add(databaseFileSelectButton);

        databaseFileLoadButton = new JButton("load");
        databaseFileLoadButton.addActionListener(actionListener);
        box.add(databaseFileLoadButton);

        return box;
    }

    private Component createRow01() {
        Box box = Box.createHorizontalBox();

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Viewer", createTabViewer());

        box.add(tabbedPane);

        return box;
    }

    private Component createTabViewer() {
        Box box = Box.createVerticalBox();

        // tool box
        Box toolBox = Box.createHorizontalBox();
        toolBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        toolBox.add(new JLabel("Regex Filter : "));

        viewerTabFilterTextField = new JTextField();
        viewerTabFilterTextField.getDocument().addDocumentListener(documentListener);
        ComponentSize.expandWidthOnly(viewerTabFilterTextField);
        toolBox.add(viewerTabFilterTextField);

        viewerTabFilterComboBox = new JComboBox<>();
        viewerTabFilterComboBox.addActionListener(actionListener);
        ComponentSize.shrinkWidthToContents(viewerTabFilterComboBox);
        toolBox.add(viewerTabFilterComboBox);

        viewerTabFilterResetButton = new JButton("reset");
        viewerTabFilterResetButton.addActionListener(actionListener);
        toolBox.add(viewerTabFilterResetButton);

        ComponentSize.shrinkHeightToContents(toolBox);
        box.add(toolBox);

        // table
        viewerTabTable = new JTable() {
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
        viewerTabTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        viewerTabTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        viewerTabTable.setRowHeight(32);
        viewerTabTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(viewerTabTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        box.add(scrollPane);

        return box;
    }

    @Override
    public void setPresenter(IDbManager.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showView() {
        setVisible(true);
    }

    @Override
    public void showDialog(String title, int messageType, Object... messages) {
        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog(this, messages, title, messageType));
    }

    @Override
    public String getText_databaseFileTextField() {
        return databaseFileTextField.getText();
    }

    @Override
    public void setTableModel_viewerTabTable(TableModel tableModel) {
        viewerTabTable.setModel(tableModel);
        TableUtil.resizeColumnWidth(viewerTabTable, 40, 400, 10);
    }

    @Override
    public void setTableRowSorter_viewerTabTable(TableRowSorter<TableModel> tableRowSorter) {
        viewerTabTable.setRowSorter(tableRowSorter);
    }

    @Override
    public void setItems_viewerTabFilterComboBox(String... items) {
        viewerTabFilterComboBox.removeAllItems();
        for (String i : items) {
            viewerTabFilterComboBox.addItem(i);
        }
    }

    @Override
    public String getText_viewerTabFilterComboBox() {
        return (String) viewerTabFilterComboBox.getSelectedItem();
    }

    @Override
    public String getText_viewerTabFilterTextField() {
        return viewerTabFilterTextField.getText();
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
            view.dispose();
        } else {
            // TODO: Remove println after completing the view.
            System.out.println(source); // NOPMD
        }
    }
}


class DbManagerViewActionListener implements ActionListener {
    private final DbManagerView view;
    private final JsonFileChooser jsonFileChooser = new JsonFileChooser();

    public DbManagerViewActionListener(DbManagerView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(view.databaseFileSelectButton)) {
            Path path = jsonFileChooser.get(view);
            if (path != null) {
                view.databaseFileTextField.setText(path.toString());
            }
        } else if (source.equals(view.databaseFileLoadButton)) {
            view.presenter.loadDatabase();
        } else if (source.equals(view.viewerTabFilterResetButton)) {
            view.viewerTabFilterTextField.setText("");
        } else if (source.equals(view.viewerTabFilterComboBox)) {
            view.presenter.updateFilter();
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
