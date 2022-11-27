package com.github.johypark97.varchivemacro.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import com.github.johypark97.varchivemacro.gui.presenter.IMacro;
import com.github.johypark97.varchivemacro.gui.view.component.RadioButtonGroup;
import com.github.johypark97.varchivemacro.gui.view.component.SliderGroup;
import com.github.johypark97.varchivemacro.gui.view.component.SliderSet;
import com.github.johypark97.varchivemacro.gui.view.util.ComponentSize;
import com.github.johypark97.varchivemacro.util.Language;
import com.github.johypark97.varchivemacro.util.Version;

public class MacroView extends JFrame implements IMacro.View {
    private static final String TITLE = "V-ARCHIVE Macro";

    private static final String EMPTY_STRING = "";
    private static final String GITHUB_URL = "https://github.com/johypark97/VArchiveMacro";
    private static final String NEWLINE = System.lineSeparator();
    private static final int LOG_LINES = 100;

    private static final int SLIDER_GROUP_TEXT_FIELD_COLUMNS = 4;
    private static final int SLIDER_GROUP_WIDTH = 500;

    private static final int COUNT_LIMIT_MAX = 10000;
    private static final int COUNT_SLIDER_MAX = 1000;
    private static final int COUNT_SLIDER_MIN = 0;
    private static final int COUNT_SLIDER_TICK_MAJOR = 200;
    private static final int COUNT_SLIDER_TICK_MINOR = 50;

    private static final int MOVING_DELAY_LIMIT_MAX = 5000;
    private static final int MOVING_DELAY_SLIDER_MAX = 2000;
    private static final int MOVING_DELAY_SLIDER_MIN = 200;
    private static final int MOVING_DELAY_SLIDER_TICK_MAJOR = 300;
    private static final int MOVING_DELAY_SLIDER_TICK_MINOR = 100;

    private static final int CAPTURE_DURATION_LIMIT_MAX = 1000;
    private static final int CAPTURE_DURATION_SLIDER_MAX = 100;
    private static final int CAPTURE_DURATION_SLIDER_MIN = 0;
    private static final int CAPTURE_DURATION_SLIDER_TICK_MAJOR = 20;
    private static final int CAPTURE_DURATION_SLIDER_TICK_MINOR = 5;

    private static final int INPUT_DURATION_LIMIT_MAX = 1000;
    private static final int INPUT_DURATION_SLIDER_MAX = 100;
    private static final int INPUT_DURATION_SLIDER_MIN = 0;
    private static final int INPUT_DURATION_SLIDER_TICK_MAJOR = 20;
    private static final int INPUT_DURATION_SLIDER_TICK_MINOR = 5;

    private static final int LOG_FONT_SIZE = 12;
    private static final int LOG_ROWS = 8;

    // components
    private JButton advancedButton;
    private JTextArea logTextArea;
    private JTextArea optionTextArea;
    private RadioButtonGroup<AnalyzeKey> analyzeKeyRadioGroup;
    private RadioButtonGroup<DirectionKey> directionKeyRadioGroup;
    private SliderGroup<SliderKey> advancedSliderGroup;
    private SliderGroup<SliderKey> generalSliderGroup;
    private TitledBorder analyzeKeyBorder;
    private TitledBorder directionKeyBorder;
    private TitledBorder optionBorder;
    private TitledBorder sliderBorder;
    protected JMenu menuFile;
    protected JMenu menuInfo;
    protected JMenuItem menuItemAbout;
    protected JMenuItem menuItemExit;
    protected JMenuItem menuItemOSL;

    // event listeners
    private ActionListener menuListener = new MacroViewMenuListener(this);
    private WindowListener windowListener = new MacroViewWindowListener(this);

    // variables for frame size controlling
    private int advancedHeight;
    private int minimumHeight;

    // variables
    private Language lang = Language.getInstance();
    public IMacro.Presenter presenter;

    public MacroView() {
        setTitle(TITLE + " v" + Version.version);
        setFrameOption();
        setContentPanel();
        setContent();
        setText();
        packSize();

        addWindowListener(windowListener);
    }

    public void showView() {
        setVisible(true);
    }

    private void setFrameOption() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/overMeElFail.png")).getImage());
        setResizable(true);
    }

    private void setContentPanel() {
        setContentPane(new JPanel(new BorderLayout()));
    }

    private void packSize() {
        setMinimumSize(new Dimension());

        pack();
        advancedHeight = advancedSliderGroup.getHeight();
        advancedSliderGroup.setVisible(false);

        pack();
        minimumHeight = getHeight();

        ComponentSize.preventShrink(this);
        setLocationRelativeTo(null);
    }

    private void setContent() {
        add(createMenu(), BorderLayout.PAGE_START);

        Box box = Box.createVerticalBox();
        box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        box.add(createRow00());
        box.add(createRow01());
        box.add(createRow02());

        add(box, BorderLayout.CENTER);
    }

    private Component createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // menu file
        menuFile = new JMenu();

        menuItemExit = new JMenuItem();
        menuItemExit.addActionListener(menuListener);
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        // menu info
        menuInfo = new JMenu();

        menuItemOSL = new JMenuItem();
        menuItemOSL.addActionListener(menuListener);
        menuInfo.add(menuItemOSL);

        menuInfo.addSeparator();

        menuItemAbout = new JMenuItem();
        menuItemAbout.addActionListener(menuListener);
        menuInfo.add(menuItemAbout);

        menuBar.add(menuInfo);

        return menuBar;
    }

    private Component createRow00() {
        Box box = Box.createVerticalBox();
        sliderBorder = BorderFactory.createTitledBorder(EMPTY_STRING);
        box.setBorder(sliderBorder);
        ComponentSize.expandWidthOnly(box);
        ComponentSize.shrinkHeightToContents(box);

        // general slider
        generalSliderGroup = new SliderGroup<>();
        SliderSet count = generalSliderGroup.addNewSliderSet(SliderKey.COUNT);
        count.setLimitMax(COUNT_LIMIT_MAX);
        count.getSlider().setMaximum(COUNT_SLIDER_MAX);
        count.getSlider().setMinimum(COUNT_SLIDER_MIN);
        count.getSlider().setMajorTickSpacing(COUNT_SLIDER_TICK_MAJOR);
        count.getSlider().setMinorTickSpacing(COUNT_SLIDER_TICK_MINOR);
        SliderSet movingDelay = generalSliderGroup.addNewSliderSet(SliderKey.MOVING_DELAY);
        movingDelay.setLimitMax(MOVING_DELAY_LIMIT_MAX);
        movingDelay.getSlider().setMaximum(MOVING_DELAY_SLIDER_MAX);
        movingDelay.getSlider().setMinimum(MOVING_DELAY_SLIDER_MIN);
        movingDelay.getSlider().setMajorTickSpacing(MOVING_DELAY_SLIDER_TICK_MAJOR);
        movingDelay.getSlider().setMinorTickSpacing(MOVING_DELAY_SLIDER_TICK_MINOR);
        generalSliderGroup.setupLayout();
        generalSliderGroup.forEachSliders((x) -> {
            x.setPaintLabels(true);
            x.setPaintTicks(true);
        });
        generalSliderGroup.forEachTextFields((x) -> {
            x.setColumns(SLIDER_GROUP_TEXT_FIELD_COLUMNS);
            x.setHorizontalAlignment(JTextField.RIGHT);
            ComponentSize.preventExpand(x);
        });
        generalSliderGroup.setWidth(SLIDER_GROUP_WIDTH);

        Box generalSliderBox = Box.createHorizontalBox();
        generalSliderBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        generalSliderBox.add(generalSliderGroup);
        box.add(generalSliderBox);

        // separator
        Box separatorBox = Box.createHorizontalBox();
        separatorBox.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        separatorBox.add(new JSeparator());
        box.add(separatorBox);

        // advanced button
        advancedButton = new JButton();
        advancedButton.setFocusPainted(false);
        advancedButton.addActionListener((e) -> {
            int width = getMinimumSize().width;
            if (!advancedSliderGroup.isVisible()) {
                advancedSliderGroup.setVisible(true);
                setSize(getWidth(), getHeight() + advancedHeight);
                setMinimumSize(new Dimension(width, minimumHeight + advancedHeight));
            } else {
                advancedSliderGroup.setVisible(false);
                setMinimumSize(new Dimension(width, minimumHeight));
                setSize(getWidth(), getHeight() - advancedHeight);
            }
        });
        advancedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        ComponentSize.expandWidthOnly(advancedButton);
        box.add(advancedButton);

        // advanced slider
        advancedSliderGroup = new SliderGroup<>();
        SliderSet captureDuration = advancedSliderGroup.addNewSliderSet(SliderKey.CAPTURE_DURATION);
        captureDuration.setLimitMax(CAPTURE_DURATION_LIMIT_MAX);
        captureDuration.getSlider().setMaximum(CAPTURE_DURATION_SLIDER_MAX);
        captureDuration.getSlider().setMinimum(CAPTURE_DURATION_SLIDER_MIN);
        captureDuration.getSlider().setMajorTickSpacing(CAPTURE_DURATION_SLIDER_TICK_MAJOR);
        captureDuration.getSlider().setMinorTickSpacing(CAPTURE_DURATION_SLIDER_TICK_MINOR);
        SliderSet inputDuration = advancedSliderGroup.addNewSliderSet(SliderKey.INPUT_DURATION);
        inputDuration.setLimitMax(INPUT_DURATION_LIMIT_MAX);
        inputDuration.getSlider().setMaximum(INPUT_DURATION_SLIDER_MAX);
        inputDuration.getSlider().setMinimum(INPUT_DURATION_SLIDER_MIN);
        inputDuration.getSlider().setMajorTickSpacing(INPUT_DURATION_SLIDER_TICK_MAJOR);
        inputDuration.getSlider().setMinorTickSpacing(INPUT_DURATION_SLIDER_TICK_MINOR);
        advancedSliderGroup.setupLayout();
        advancedSliderGroup.forEachSliders((x) -> {
            x.setPaintLabels(true);
            x.setPaintTicks(true);
        });
        advancedSliderGroup.forEachTextFields((x) -> {
            x.setColumns(SLIDER_GROUP_TEXT_FIELD_COLUMNS);
            x.setHorizontalAlignment(JTextField.RIGHT);
            ComponentSize.preventExpand(x);
        });
        advancedSliderGroup.setWidth(SLIDER_GROUP_WIDTH);

        Box advancedSliderBox = Box.createHorizontalBox();
        advancedSliderBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        advancedSliderBox.add(advancedSliderGroup);
        box.add(advancedSliderBox);

        return box;
    }

    private Component createRow01() {
        Box box = Box.createHorizontalBox();

        box.add(createRow01_left());
        box.add(createRow01_right());

        return box;
    }

    private Component createRow01_left() {
        Box box = Box.createHorizontalBox();
        analyzeKeyBorder = BorderFactory.createTitledBorder(EMPTY_STRING);
        box.setBorder(analyzeKeyBorder);

        analyzeKeyRadioGroup = new RadioButtonGroup<>();
        analyzeKeyRadioGroup.setLayout(new BoxLayout(analyzeKeyRadioGroup, BoxLayout.X_AXIS));
        analyzeKeyRadioGroup.addButton(AnalyzeKey.ALT_F11);
        analyzeKeyRadioGroup.addButton(AnalyzeKey.ALT_F12);
        analyzeKeyRadioGroup.addButton(AnalyzeKey.ALT_HOME);
        analyzeKeyRadioGroup.addButton(AnalyzeKey.ALT_INS);

        box.add(Box.createHorizontalGlue());
        box.add(analyzeKeyRadioGroup);
        box.add(Box.createHorizontalGlue());

        return box;
    }

    private Component createRow01_right() {
        Box box = Box.createHorizontalBox();
        directionKeyBorder = BorderFactory.createTitledBorder(EMPTY_STRING);
        box.setBorder(directionKeyBorder);

        directionKeyRadioGroup = new RadioButtonGroup<>();
        directionKeyRadioGroup.setLayout(new BoxLayout(directionKeyRadioGroup, BoxLayout.X_AXIS));
        directionKeyRadioGroup.addButton(DirectionKey.UP);
        directionKeyRadioGroup.addButton(DirectionKey.DOWN);

        box.add(Box.createHorizontalGlue());
        box.add(directionKeyRadioGroup);
        box.add(Box.createHorizontalGlue());

        return box;
    }

    private Component createRow02() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(createRow02_left(), BorderLayout.CENTER);
        panel.add(createRow02_right(), BorderLayout.LINE_END);

        return panel;
    }

    private Component createRow02_left() {
        logTextArea = new JTextArea(LOG_ROWS, 0);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, LOG_FONT_SIZE));

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        Border outsideBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border insideBorder = BorderFactory.createLoweredBevelBorder();
        scrollPane.setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));

        return scrollPane;
    }

    private Component createRow02_right() {
        Box box = Box.createVerticalBox();
        optionBorder = BorderFactory.createTitledBorder(EMPTY_STRING);
        box.setBorder(optionBorder);

        optionTextArea = new JTextArea();
        optionTextArea.setEditable(false);
        optionTextArea.setFocusable(false);
        optionTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, LOG_FONT_SIZE));
        optionTextArea.setOpaque(false);

        box.add(optionTextArea);
        box.add(Box.createVerticalGlue());

        return box;
    }

    private void setText() {
        menuFile.setText(lang.get("v.menu.file"));
        menuItemExit.setText(lang.get("v.menu.file.exit"));

        menuInfo.setText(lang.get("v.menu.info"));
        menuItemOSL.setText(lang.get("v.menu.info.osl"));
        menuItemAbout.setText(lang.get("v.menu.info.about"));

        sliderBorder.setTitle(lang.get("v.title.capture"));
        advancedButton.setText(lang.get("v.advanced_button"));

        generalSliderGroup.getSliderSet(SliderKey.COUNT).getLabel()
                .setText(lang.get("v.slider.count"));
        generalSliderGroup.getSliderSet(SliderKey.MOVING_DELAY).getLabel()
                .setText(lang.get("v.slider.moving_delay"));

        JLabel captureDuration =
                advancedSliderGroup.getSliderSet(SliderKey.CAPTURE_DURATION).getLabel();
        captureDuration.setText(lang.get("v.slider.capture_duration"));
        captureDuration.setToolTipText(lang.get("v.slider.capture_duration.tooltip"));

        JLabel inputDuration =
                advancedSliderGroup.getSliderSet(SliderKey.INPUT_DURATION).getLabel();
        inputDuration.setText(lang.get("v.slider.input_duration"));
        inputDuration.setToolTipText(lang.get("v.slider.input_duration.tooltip"));

        generalSliderGroup.forEachSliders((x) -> {
            x.setToolTipText(lang.get("v.right_click_reset"));
        });

        advancedSliderGroup.forEachSliders((x) -> {
            x.setToolTipText(lang.get("v.right_click_reset"));
        });

        analyzeKeyBorder.setTitle(lang.get("v.title.analyze_key"));
        analyzeKeyRadioGroup.getButton(AnalyzeKey.ALT_F11).setText("[Alt + F11]");
        analyzeKeyRadioGroup.getButton(AnalyzeKey.ALT_F12).setText("[Alt + F12]");
        analyzeKeyRadioGroup.getButton(AnalyzeKey.ALT_HOME).setText("[Alt + Home]");
        analyzeKeyRadioGroup.getButton(AnalyzeKey.ALT_INS).setText("[Alt + Ins]");

        directionKeyBorder.setTitle(lang.get("v.title.direction_key"));
        directionKeyRadioGroup.getButton(DirectionKey.DOWN)
                .setText(lang.get("v.direction_key.down"));
        directionKeyRadioGroup.getButton(DirectionKey.UP).setText(lang.get("v.direction_key.up"));

        optionBorder.setTitle(lang.get("v.title.option"));

        String[] keyInfo = new String[4];
        keyInfo[0] = String.format("[Home]: %s", lang.get("v.key.start"));
        keyInfo[1] = String.format("[End]: %s", lang.get("v.key.stop"));
        keyInfo[2] = String.format("[Ctrl + Up]: %s", lang.get("v.key.up"));
        keyInfo[3] = String.format("[Ctrl + Down]: %s", lang.get("v.key.down"));
        optionTextArea.setText(String.join(NEWLINE, keyInfo));
    }

    protected void showAbout() {
        showDialog(
                lang.get("v.menu.info.about"), new Object[] {"V-Archive Macro",
                        "Version: " + Version.version, "Soruce Code: " + GITHUB_URL},
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setSliderDefault(int count, int movingDelay, int captureDuration,
            int inputDuration) {
        generalSliderGroup.getSliderSet(SliderKey.COUNT).setDefault(count);
        generalSliderGroup.getSliderSet(SliderKey.MOVING_DELAY).setDefault(movingDelay);
        advancedSliderGroup.getSliderSet(SliderKey.CAPTURE_DURATION).setDefault(captureDuration);
        advancedSliderGroup.getSliderSet(SliderKey.INPUT_DURATION).setDefault(inputDuration);
    }

    @Override
    public void setValues(int count, int movingDelay, int captureDuration, int inputDuration,
            AnalyzeKey analyzeKey, DirectionKey directionKey) {
        generalSliderGroup.getSliderSet(SliderKey.COUNT).setValue(count);
        generalSliderGroup.getSliderSet(SliderKey.MOVING_DELAY).setValue(movingDelay);
        advancedSliderGroup.getSliderSet(SliderKey.CAPTURE_DURATION).setValue(captureDuration);
        advancedSliderGroup.getSliderSet(SliderKey.INPUT_DURATION).setValue(inputDuration);
        analyzeKeyRadioGroup.setSelected(analyzeKey);
        directionKeyRadioGroup.setSelected(directionKey);
    }

    @Override
    public int getCount() {
        return generalSliderGroup.getSliderSet(SliderKey.COUNT).getValue();
    }

    @Override
    public int getMovingDelay() {
        return generalSliderGroup.getSliderSet(SliderKey.MOVING_DELAY).getValue();
    }

    @Override
    public int getCaptureDuration() {
        return advancedSliderGroup.getSliderSet(SliderKey.CAPTURE_DURATION).getValue();
    }

    @Override
    public int getInputDuration() {
        return advancedSliderGroup.getSliderSet(SliderKey.INPUT_DURATION).getValue();
    }

    @Override
    public AnalyzeKey getAnalyzeKey() {
        return analyzeKeyRadioGroup.getSelected();
    }

    @Override
    public DirectionKey getDirectionKey() {
        return directionKeyRadioGroup.getSelected();
    }

    @Override
    public void setDirectionKey(DirectionKey key) {
        directionKeyRadioGroup.setSelected(key);
    }

    @Override
    public void showDialog(String title, Object[] messages, int messageType) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, messages, title, messageType);
        });
    }

    @Override
    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            if (logTextArea.getLineCount() > LOG_LINES) {
                try {
                    int index = logTextArea.getLineEndOffset(0);
                    logTextArea.replaceRange("", 0, index);
                } catch (BadLocationException e) {
                }
            }

            logTextArea.append(message);
            logTextArea.append(NEWLINE);
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }
}


class MacroViewWindowListener implements WindowListener {
    private MacroView view;

    public MacroViewWindowListener(MacroView view) {
        this.view = view;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        view.presenter.viewOpened();
    }

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {
        view.presenter.viewClosed();

        System.runFinalization();
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}


class MacroViewMenuListener implements ActionListener {
    private MacroView view;

    public MacroViewMenuListener(MacroView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == view.menuItemExit)
            view.dispose();
        else if (source == view.menuItemOSL)
            view.presenter.showLicense();
        else if (source == view.menuItemAbout)
            view.showAbout();
    }
}
