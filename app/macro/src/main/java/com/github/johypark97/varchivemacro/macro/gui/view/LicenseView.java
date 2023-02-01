package com.github.johypark97.varchivemacro.macro.gui.view;

import com.github.johypark97.varchivemacro.macro.gui.presenter.ILicense.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.ILicense.View;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serial;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class LicenseView extends JDialog implements View, WindowListener {
    @Serial
    private static final long serialVersionUID = -8568849941252758711L;

    private static final String WINDOW_TITLE = "Open Source License";
    private static final int LIST_FONT_SIZE = 14;
    private static final int TEXT_AREA_FONT_SIZE = 12;
    private static final int WINDOW_HEIGHT = 480;
    private static final int WINDOW_WIDTH = 640;

    // presenter
    public transient Presenter presenter;

    // components
    private JList<String> list;
    private JTextArea textArea;

    public LicenseView(JFrame parent) {
        super(parent, WINDOW_TITLE, true);

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
        add(createList(), BorderLayout.LINE_START);
        add(createTextArea(), BorderLayout.CENTER);
    }

    private Component createList() {
        list = new JList<>();
        list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, LIST_FONT_SIZE));

        list.addListSelectionListener((e) -> {
            String value = list.getSelectedValue();
            if (value != null) {
                textArea.setText(presenter.getLicense(value));
                textArea.setCaretPosition(0);
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        return scrollPane;
    }

    private Component createTextArea() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, TEXT_AREA_FONT_SIZE));

        JScrollPane scrollPane = new JScrollPane(textArea);
        Border in = BorderFactory.createLineBorder(Color.GRAY);
        Border out = BorderFactory.createEmptyBorder(0, 5, 0, 0);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(out, in));

        return scrollPane;
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
    public void setLicenses(List<String> licenses) {
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(licenses);
        list.setModel(model);
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
