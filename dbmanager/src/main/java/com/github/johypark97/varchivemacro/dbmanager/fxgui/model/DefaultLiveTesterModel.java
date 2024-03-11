package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.StartData;
import com.github.johypark97.varchivemacro.lib.common.AwtRobotHelper;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;

public class DefaultLiveTesterModel implements LiveTesterModel {
    private CollectionArea area;
    private OcrWrapper ocr;
    private Robot robot;
    private TitleSongRecognizer<LocalDlcSong> recognizer;

    @Override
    public void initialize(List<LocalDlcSong> dlcSongList, TitleTool titleTool, StartData data)
            throws AWTException, NotSupportedResolutionException, OcrInitializationError {
        ocr = new DefaultOcrWrapper(data.tessdataPath, data.tessdataLanguage);
        robot = new Robot();

        BufferedImage image = AwtRobotHelper.captureScreenshot(robot);
        area = CollectionAreaFactory.create(new Dimension(image.getWidth(), image.getHeight()));

        recognizer = new TitleSongRecognizer<>(titleTool);
        recognizer.setSongList(dlcSongList);
    }

    @Override
    public void terminate() {
        ocr.close();

        area = null; // NOPMD
        ocr = null; // NOPMD
        recognizer = null; // NOPMD
        robot = null; // NOPMD
    }

    @Override
    public RecognizedData recognize() throws IOException, PixError {
        RecognizedData data = new RecognizedData();

        BufferedImage screenshot = AwtRobotHelper.captureScreenshot(robot);
        BufferedImage titleImage = area.getTitle(screenshot);
        data.image = SwingFXUtils.toFXImage(titleImage, null);

        String title;
        try (PixWrapper pix = new PixWrapper(ImageConverter.imageToPngBytes(titleImage))) {
            PixPreprocessor.preprocessTitle(pix);
            title = ocr.run(pix.pixInstance).trim();
        }
        data.text = title;

        Recognized<LocalDlcSong> recognized = recognizer.recognize(title);
        data.recognized = switch (recognized.status()) {
            case DUPLICATED_SONG -> "duplicated";
            case FOUND -> {
                LocalDlcSong song = recognized.song();
                yield String.format("%s - %s (accuracy: %f, distance: %d)", song.title,
                        song.composer, recognized.similarity(), recognized.distance());
            }
            case NOT_FOUND -> "not found";
        };

        return data;
    }
}
