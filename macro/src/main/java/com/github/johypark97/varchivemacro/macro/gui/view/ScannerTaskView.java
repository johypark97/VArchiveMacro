package com.github.johypark97.varchivemacro.macro.gui.view;

import com.github.johypark97.varchivemacro.lib.desktop.gui.util.ComponentSize;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.ResponseData.RecordData;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.View;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.johypark97.varchivemacro.macro.resource.ScannerTaskViewKey;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serial;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ScannerTaskView extends JDialog implements View, WindowListener {
    @Serial
    private static final long serialVersionUID = 1403466149083029200L;

    private static final int WINDOW_HEIGHT = 480;
    private static final int WINDOW_WIDTH = 960;

    // language
    private transient final Language lang = Language.getInstance();

    // components
    private transient final Table<Button, Pattern, RecordBox> recordBoxes = HashBasedTable.create();
    private JLabel fullImage;
    private JLabel titleImage;

    // presenter
    private transient Presenter presenter;

    public ScannerTaskView(JFrame parent) {
        super(parent, false);

        setTitle(lang.get(ScannerTaskViewKey.WINDOW_TITLE));
        setFrameOption();
        setContentPanel();
        setContent();

        addWindowListener(this);
    }

    private void setFrameOption() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setFocusableWindowState(false);
        setMinimumSize(new Dimension(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2));
        setResizable(true);
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        setLocationRelativeTo(null);
    }

    private void setContentPanel() {
        setContentPane(new JPanel(new BorderLayout()));
    }

    private void setContent() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(lang.get(ScannerTaskViewKey.TAB_RESULT), createResultTab());
        tabbedPane.addTab(lang.get(ScannerTaskViewKey.TAB_FULL_IMAGE), createFullImageTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private Component createResultTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(
                BorderFactory.createTitledBorder(lang.get(ScannerTaskViewKey.TITLE_IMAGE)));
        {
            titleImage = new JLabel();
            titlePanel.add(titleImage);
        }
        panel.add(titlePanel, BorderLayout.CENTER);

        // record grid
        JPanel recordGrid = new JPanel();
        recordGrid.setBorder(
                BorderFactory.createTitledBorder(lang.get(ScannerTaskViewKey.RECORD_IMAGE)));
        {
            int rows = Pattern.values().length + 1;
            int columns = Button.values().length + 1;
            recordGrid.setLayout(new GridLayout(rows, columns, 0, 10));

            // header
            recordGrid.add(new JLabel());
            for (Button button : Button.values()) {
                JLabel label = new JLabel(button + "B");
                label.setHorizontalAlignment(SwingConstants.CENTER);
                recordGrid.add(label);
            }

            // rows
            for (Pattern pattern : Pattern.values()) {
                JLabel label = new JLabel(pattern.toString());
                label.setHorizontalAlignment(SwingConstants.CENTER);
                recordGrid.add(label);

                for (Button button : Button.values()) {
                    RecordBox recordBox = new RecordBox();
                    recordBoxes.put(button, pattern, recordBox);
                    recordGrid.add(recordBox.box);
                }
            }
        }
        panel.add(recordGrid, BorderLayout.PAGE_END);

        return panel;
    }

    private Component createFullImageTab() {
        fullImage = new JLabel();

        JScrollPane scrollPane = new JScrollPane(fullImage);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

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
    public void setData(ResponseData data) {
        // full image
        fullImage.setIcon(new ImageIcon(data.fullImage));

        // title image
        titleImage.setIcon(new ImageIcon(data.titleImage));

        // records
        recordBoxes.cellSet().forEach((cell) -> {
            RecordData recordData = data.recordTable.get(cell.getRowKey(), cell.getColumnKey());
            if (recordData != null) {
                cell.getValue().setData(recordData);
            } else {
                cell.getValue().clear();
            }
        });
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


class RecordBox {
    public final Box box = Box.createVerticalBox();

    public final JLabel maxComboImage = new JLabel();
    public final JLabel rateImage = new JLabel();
    public final JCheckBox maxComboCheckbox = new JCheckBox("MaxCombo");
    public final JTextField rateTextField = new JTextField(5);

    public RecordBox() {
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        Box imageBox = Box.createHorizontalBox();
        {
            imageBox.add(rateImage);
            imageBox.add(Box.createHorizontalStrut(5));
            imageBox.add(maxComboImage);
        }
        box.add(imageBox);

        Box dataBox = Box.createHorizontalBox();
        {
            rateTextField.setBackground(Color.WHITE);
            rateTextField.setEditable(false);
            ComponentSize.fixToPreferredSize(rateTextField);
            dataBox.add(rateTextField);

            dataBox.add(maxComboCheckbox);
        }
        box.add(dataBox);
    }

    public void clear() {
        maxComboCheckbox.setSelected(false);
        maxComboImage.setIcon(null);
        rateImage.setIcon(null);
        rateTextField.setText("");
    }

    public void setData(RecordData data) {
        maxComboCheckbox.setSelected(data.maxCombo);
        maxComboImage.setIcon(new ImageIcon(data.maxComboImage));
        rateImage.setIcon(new ImageIcon(data.rateImage));
        rateTextField.setText(data.rate);
    }
}
