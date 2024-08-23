package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.StartData;
import com.github.johypark97.varchivemacro.lib.desktop.AwtRobotHelper;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
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
import java.util.stream.Collectors;
import javafx.embed.swing.SwingFXUtils;

public class DefaultLiveTesterModel implements LiveTesterModel {
    private CollectionArea area;
    private OcrWrapper ocr;
    private Robot robot;
    private TitleSongRecognizer recognizer;

    @Override
    public void initialize(List<Song> songList, TitleTool titleTool, StartData data)
            throws AWTException, NotSupportedResolutionException, OcrInitializationError {
        ocr = new DefaultOcrWrapper(data.tessdataPath, data.tessdataLanguage);
        robot = new Robot();

        BufferedImage image = AwtRobotHelper.captureScreenshot(robot);
        area = CollectionAreaFactory.create(new Dimension(image.getWidth(), image.getHeight()));

        recognizer = new TitleSongRecognizer(titleTool);
        recognizer.setSongList(songList);
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

        Recognized recognized = recognizer.recognize(title);
        data.recognized = recognized.foundList().isEmpty()
                ? "not found"
                : recognized.foundList().stream()
                        .map(x -> String.format("%s - %s [%d, %.2f]", x.song().title(),
                                x.song().composer(), x.distance(), x.similarity()))
                        .collect(Collectors.joining(", "));

        return data;
    }
}
