package com.github.johypark97.varchivemacro.dbmanager.gui.view;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.ILiveTester.Presenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.ILiveTester.View;
import com.github.johypark97.varchivemacro.lib.common.gui.component.GrowBoxCreator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LiveTesterView implements View {
    private DefaultView view;
    private Presenter presenter;

    @Override
    public void onLinkView(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public synchronized boolean isActive() {
        return view != null;
    }

    @Override
    public synchronized void resetView() {
        view = null; // NOPMD
    }

    @Override
    public synchronized void showView(JFrame parent) {
        if (isActive()) {
            return;
        }

        view = new DefaultView(parent);
        view.setVisible(true);
    }

    @Override
    public synchronized void disposeView() {
        if (isActive()) {
            view.dispose();
        }
    }

    @Override
    public void showResult(BufferedImage image, String scannedText, String note) {
        DefaultView defaultView = view;
        SwingUtilities.invokeLater(() -> {
            defaultView.setTestImage(image);
            defaultView.setTestNoteText(note);
            defaultView.setTestScannedText(scannedText);
        });
    }

    private class DefaultView extends JDialog { // NOPMD
        private static final String TITLE = "Title Live Tester";
        private static final int WINDOW_HEIGHT = 256;
        private static final int WINDOW_WIDTH = 1024;

        // components
        private JLabel testImage;
        private JTextField testNoteText;
        private JTextField testScannedText;

        public DefaultView(JFrame parent) {
            super(parent, false);

            setTitle(TITLE);
            setFrameOption();
            setContent();

            addWindowListener(new DefaultViewWindowListener(presenter));
        }

        public void setTestImage(Image image) {
            testImage.setIcon(new ImageIcon(image));
        }

        public void setTestScannedText(String text) {
            testScannedText.setText(text);
        }

        public void setTestNoteText(String text) {
            testNoteText.setText(text);
        }

        private void setFrameOption() {
            setAlwaysOnTop(true);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            setMinimumSize(new Dimension(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2));
            setResizable(true);
            setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            setLocationRelativeTo(null);
        }

        private void setContent() {
            testImage = new JLabel();
            testImage.setBorder(BorderFactory.createTitledBorder("image"));
            add(testImage, BorderLayout.CENTER);

            GrowBoxCreator endBoxCreator = new GrowBoxCreator();
            {
                testScannedText = new JTextField();
                testScannedText.setBorder(BorderFactory.createTitledBorder("scannedText"));
                endBoxCreator.add(testScannedText);

                testNoteText = new JTextField();
                testNoteText.setBorder(BorderFactory.createTitledBorder("note"));
                endBoxCreator.add(testNoteText);
            }
            add(endBoxCreator.create(), BorderLayout.PAGE_END);
        }
    }
}


class DefaultViewWindowListener implements WindowListener {
    private final Presenter presenter;

    public DefaultViewWindowListener(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        presenter.viewOpened();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        presenter.viewClosing();
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
