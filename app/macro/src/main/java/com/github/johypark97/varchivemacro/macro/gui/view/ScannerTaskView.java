package com.github.johypark97.varchivemacro.macro.gui.view;

import com.github.johypark97.varchivemacro.lib.common.gui.util.ComponentSize;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.ScannerTaskViewData;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.ScannerTaskViewData.RecordData;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.View;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String WINDOW_TITLE = "Captured Image";
    private static final int WINDOW_HEIGHT = 480;
    private static final int WINDOW_WIDTH = 960;

    // presenter
    public transient Presenter presenter;

    // components
    private ImageIcon fullImage;
    private ImageIcon titleImage;
    private transient final Map<String, RecordBox> recordBoxes = new HashMap<>();

    public ScannerTaskView(JFrame parent) {
        super(parent, WINDOW_TITLE, false);

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
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Analyzed image", createAnalyzedImageTab());
        tabbedPane.addTab("Full image", createFullImageTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private Component createAnalyzedImageTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createTitledBorder("Title"));
        {
            titleImage = new ImageIcon();
            titlePanel.add(new JLabel(titleImage));
        }
        panel.add(titlePanel, BorderLayout.CENTER);

        // record grid
        JPanel recordGrid = new JPanel(new GridLayout(5, 5, 0, 10));
        recordGrid.setBorder(BorderFactory.createTitledBorder("Records"));
        {
            List<String> rows = List.of("", "NM", "HD", "MX", "SC");
            List<String> columns = List.of("", "4B", "5B", "6B", "8B");

            rows.forEach((row) -> columns.forEach((column) -> {
                if (row.isBlank()) {
                    if (column.isBlank()) {
                        recordGrid.add(new JLabel());
                    } else {
                        JLabel label = new JLabel(column);
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        recordGrid.add(label);
                    }
                } else {
                    if (column.isBlank()) {
                        JLabel label = new JLabel(row);
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        recordGrid.add(label);
                    } else {
                        RecordBox recordBox = new RecordBox();
                        String key = String.format("_%s_%s", column, row);
                        recordBoxes.put(key, recordBox);

                        recordGrid.add(recordBox.box);
                    }
                }
            }));
        }
        panel.add(recordGrid, BorderLayout.PAGE_END);

        return panel;
    }

    private Component createFullImageTab() {
        fullImage = new ImageIcon();

        JScrollPane scrollPane = new JScrollPane(new JLabel(fullImage));
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
    public void setData(ScannerTaskViewData viewData) {
        // full image
        fullImage.setImage(viewData.fullImage);

        // title image
        titleImage.setImage(viewData.titleImage);

        // records
        recordBoxes.forEach((key, value) -> {
            RecordData data = viewData.records.get(key);
            if (data != null) {
                value.setData(data);
            }
        });

        repaint();
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

    public final ImageIcon maxComboImage = new ImageIcon();
    public final ImageIcon rateImage = new ImageIcon();
    public final JCheckBox maxComboCheckbox = new JCheckBox("MaxCombo");
    public final JTextField rateTextField = new JTextField(5);

    public RecordBox() {
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        Box imageBox = Box.createHorizontalBox();
        {
            imageBox.add(new JLabel(rateImage));
            imageBox.add(Box.createHorizontalStrut(5));
            imageBox.add(new JLabel(maxComboImage));
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

    public void setData(RecordData data) {
        maxComboCheckbox.setSelected(data.maxCombo);
        maxComboImage.setImage(data.maxComboImage);
        rateImage.setImage(data.rateImage);
        rateTextField.setText(data.rate);
    }
}
