package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.ILiveTester.Presenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.ILiveTester.View;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.LiveTesterConfig;
import com.github.johypark97.varchivemacro.lib.common.HookWrapper;
import com.github.johypark97.varchivemacro.lib.common.ImageConverter;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.common.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.lib.common.recognizer.TitleSongRecognizer;
import com.github.johypark97.varchivemacro.lib.common.recognizer.TitleSongRecognizer.Recognized;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFrame;

public class LiveTesterPresenter implements Presenter {
    private final NativeKeyListener nativeKeyListener = new LiveTesterNativeKeyListener();

    private CollectionArea area;
    private OcrWrapper ocrWrapper;
    private Rectangle screenRect;
    private Robot robot;
    private TitleSongRecognizer recognizer;
    private View view;

    private synchronized void runTest() {
        BufferedImage image = robot.createScreenCapture(screenRect);
        image = area.getTitle(image);

        String scannedText;
        try (PixWrapper pix = new PixWrapper(ImageConverter.imageToPngBytes(image))) {
            PixPreprocessor.preprocessTitle(pix);

            image = ImageConverter.pngBytesToImage(pix.getPngBytes());
            scannedText = ocrWrapper.run(pix.pixInstance);
        } catch (PixError | IOException e) {
            view.showResult(null, e.getMessage(), null);
            return;
        }

        Recognized recognized = recognizer.recognize(scannedText);
        String note = switch (recognized.status()) {
            case DUPLICATED_SONG -> String.format("duplicated (%s, %d, %f)", recognized.foundKey(),
                    recognized.distance(), recognized.similarity());
            case FOUND -> {
                LocalSong song = recognized.song();
                yield String.format("%s - %s (%s) (%s, %d, %f)", song.title(), song.composer(),
                        song.dlc(), recognized.foundKey(), recognized.distance(),
                        recognized.similarity());
            }
            case NOT_FOUND -> "not found";
        };

        view.showResult(image, scannedText, note);
    }

    @Override
    public synchronized void linkView(View view) {
        this.view = view;
        this.view.onLinkView(this);
    }

    @Override
    public synchronized void start(JFrame parent, SongModel songModel, LiveTesterConfig config)
            throws OcrInitializationError, NotSupportedResolutionException, AWTException,
            IOException {
        if (view.isActive()) {
            return;
        }

        if (config.trainedDataDirectory.equals(Path.of(""))) {
            throw new IOException("directory not designated");
        } else if (!Files.isDirectory(config.trainedDataDirectory)) {
            throw new IOException("invalid directory");
        } else if (config.trainedDataLanguage.isBlank()) {
            throw new IOException("invalid language");
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        area = CollectionAreaFactory.create(screenSize);
        screenRect = new Rectangle(screenSize);

        ocrWrapper = new DefaultOcrWrapper(config.trainedDataDirectory, config.trainedDataLanguage);
        robot = new Robot();

        recognizer = new TitleSongRecognizer(songModel.getTitleTool());
        recognizer.setSongList(songModel.getSongList());

        view.showView(parent);
    }

    @Override
    public synchronized void stop() {
        if (!view.isActive()) {
            return;
        }

        view.disposeView();
    }

    @Override
    public void viewOpened() {
        HookWrapper.addKeyListener(nativeKeyListener);
    }

    @Override
    public void viewClosing() {
        stop();
    }

    @Override
    public synchronized void viewClosed() {
        HookWrapper.removeKeyListener(nativeKeyListener);
        ocrWrapper.close();

        view.resetView();
    }

    private class LiveTesterNativeKeyListener implements NativeKeyListener {
        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
            if (nativeEvent.getModifiers() != 0) {
                return;
            }

            if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_ENTER) {
                runTest();
            }
        }
    }
}
