package com.github.johypark97.varchivemacro.macro.gui.view;

import com.github.johypark97.varchivemacro.macro.gui.presenter.IExpected.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IExpected.View;
import com.github.johypark97.varchivemacro.macro.resource.ExpectedViewKey;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serial;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

public class ExpectedView extends JDialog implements View, WindowListener {
    @Serial
    private static final long serialVersionUID = -8933266479200356692L;

    private static final int DEFAULT_FONT_SIZE = 12;
    private static final int WINDOW_HEIGHT = 640;
    private static final int WINDOW_WIDTH = 480;

    // language
    private transient final Language lang = Language.getInstance();

    // presenter
    private transient Presenter presenter;

    // components
    private JTree tree;

    public ExpectedView(JFrame parent) {
        super(parent, true);

        setTitle(lang.get(ExpectedViewKey.WINDOW_TITLE));
        setFrameOption();
        setContentPanel();
        setContent();

        addWindowListener(this);
    }

    private void setFrameOption() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2));
        setResizable(true);
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        setLocationRelativeTo(null);
    }

    private void setContentPanel() {
        setContentPane(new JPanel(new BorderLayout()));
    }

    private void setContent() {
        Box toolBox = Box.createHorizontalBox();
        toolBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        {
            toolBox.add(new JLabel(lang.get(ExpectedViewKey.FONT_SIZE) + ": "));

            JSpinner fontSize = new JSpinner(new SpinnerNumberModel(DEFAULT_FONT_SIZE, 8, 64, 1));
            fontSize.addChangeListener((e) -> {
                int value = (int) fontSize.getValue();
                setTreeFontSize(value);
            });
            toolBox.add(fontSize);

            toolBox.add(Box.createHorizontalGlue());

            JCheckBox checkBox = new JCheckBox(lang.get(ExpectedViewKey.ALWAYS_ON_TOP));
            checkBox.addActionListener((e) -> setAlwaysOnTop(checkBox.isSelected()));
            toolBox.add(checkBox);
        }
        add(toolBox, BorderLayout.PAGE_START);

        tree = new JTree();
        tree.setModel(null);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setTreeFontSize(DEFAULT_FONT_SIZE);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    private void setTreeFontSize(int size) {
        tree.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
        tree.setRowHeight(size * 2);
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
    public void setTreeModel(TreeModel model) {
        tree.setModel(model);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
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