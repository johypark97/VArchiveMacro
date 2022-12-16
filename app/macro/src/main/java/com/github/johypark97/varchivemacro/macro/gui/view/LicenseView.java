package com.github.johypark97.varchivemacro.macro.gui.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.github.johypark97.varchivemacro.macro.gui.presenter.ILicense;

public class LicenseView extends JFrame implements ILicense.View {
    private static final String TITLE = "Open Source License";

    private static final int FRAME_HEIGHT = 600;
    private static final int FRAME_WIDTH = 800;

    private static final int LIST_FONT_SIZE = 14;
    private static final int TEXT_AREA_FONT_SIZE = 12;

    // presenter
    protected ILicense.Presenter presenter;

    // components
    private DefaultListModel<String> listModel;
    private JTextArea textArea;
    protected JList<String> list;

    // event listeners
    private WindowListener windowListener = new LicenseViewWindowListener(this);
    private ListSelectionListener listListener = new LicenseViewListListener(this);

    public LicenseView() {
        setTitle(TITLE);
        setFrameOption();
        setContentPanel();
        setContent();

        addWindowListener(windowListener);
    }

    private void setFrameOption() {
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setResizable(true);
        setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        setLocationRelativeTo(null);
    }

    private void setContentPanel() {
        Box box = Box.createVerticalBox();
        box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(box);
    }

    private void setContent() {
        Component left = createLeft();
        Component right = createRight();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(10);

        add(splitPane);
    }

    private Component createLeft() {
        listModel = new DefaultListModel<>();

        list = new JList<>(listModel);
        list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, LIST_FONT_SIZE));

        list.addListSelectionListener(listListener);

        return new JScrollPane(list);
    }

    private Component createRight() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, TEXT_AREA_FONT_SIZE));

        return new JScrollPane(textArea);
    }

    @Override
    public void setPresenter(ILicense.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showView() {
        setVisible(true);
    }

    @Override
    public void setList(String[] list) {
        listModel.clear();

        for (String i : list)
            listModel.addElement(i);
    }

    @Override
    public void showText(String text) {
        textArea.setText(text);
        textArea.setCaretPosition(0);
    }
}


class LicenseViewWindowListener implements WindowListener {
    private LicenseView view;

    public LicenseViewWindowListener(LicenseView view) {
        this.view = view;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        view.presenter.viewOpened();
    }

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}


class LicenseViewListListener implements ListSelectionListener {
    private LicenseView view;

    public LicenseViewListListener(LicenseView view) {
        this.view = view;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        view.presenter.showLicense(view.list.getSelectedValue());
    }
}
