package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData.FoundData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData.Status;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.util.CacheHelper;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.area.TrainingArea;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized;
import com.github.johypark97.varchivemacro.lib.scanner.recognizer.TitleSongRecognizer.Recognized.StatusAccuracy;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;

public class OcrTestTask extends Task<Void> {
    public List<LocalDlcSong> dlcSongList;
    public Path cachePath;
    public Path tessdataPath;
    public String tessdataLanguage;
    public TitleTool titleTool;

    public Consumer<OcrTestData> onAddData;
    public Runnable onClearData;

    @Override
    protected Void call() throws Exception {
        Objects.requireNonNull(cachePath);
        Objects.requireNonNull(dlcSongList);
        Objects.requireNonNull(tessdataLanguage);
        Objects.requireNonNull(tessdataPath);
        Objects.requireNonNull(titleTool);

        Objects.requireNonNull(onAddData);
        Objects.requireNonNull(onClearData);

        updateProgress(-1, 0);

        if (!Files.isDirectory(cachePath)) {
            throw new IOException("Invalid cache directory");
        } else if (!Files.isDirectory(tessdataPath)) {
            throw new IOException("Invalid tessdata directory");
        } else if (tessdataLanguage.isBlank()) {
            throw new IOException("Invalid tessdata language");
        }

        onClearData.run();
        updateProgress(0, 1);

        TrainingArea area = null;
        TitleSongRecognizer<LocalDlcSong> recognizer = new TitleSongRecognizer<>(titleTool);
        recognizer.setSongList(dlcSongList);

        try (OcrWrapper ocr = DefaultOcrWrapper.load(tessdataPath, tessdataLanguage)) {
            int count = dlcSongList.size();
            for (int i = 0; i < count; ++i) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                LocalDlcSong song = dlcSongList.get(i);

                Path imagePath = CacheHelper.createImagePath(cachePath, song);
                BufferedImage image = ImageIO.read(imagePath.toFile());
                if (area == null) {
                    Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
                    area = new TrainingArea(CollectionAreaFactory.create(imageSize));
                }
                image = area.getTrainingTitle(image);
                image = TrainingArea.cropTrainingMargin(image);

                String scannedTitle;
                try (PixWrapper pix = PixWrapper.load(ImageConverter.imageToPngBytes(image))) {
                    PixPreprocessor.preprocessTitle(pix);
                    scannedTitle = ocr.run(pix.pixInstance);
                }

                OcrTestData data = new OcrTestData(song);

                Recognized<LocalDlcSong> recognized = recognizer.recognize(scannedTitle);
                data.setFoundDataList(recognized.foundList.stream().map(FoundData::new).toList());
                data.setFoundKeyList(recognized.foundKeySet().stream().toList());
                data.setScannedTitle(recognized.normalizedInput);

                switch (recognized.statusFound) {
                    case FOUND_DUPLICATE_SONGS -> {
                        if (!data.targetSong.title.equals(
                                recognized.foundList.get(0).song().title)) {
                            data.setTestStatus(Status.WRONG);
                        } else {
                            data.setTestPass(true);
                            data.setTestStatus(
                                    StatusAccuracy.FOUND_EXACT.equals(recognized.statusAccuracy)
                                            ? Status.DUPLICATED_EXACT
                                            : Status.DUPLICATED_SIMILAR);
                        }
                    }
                    case FOUND_MANY_SONGS -> data.setTestStatus(Status.WRONG);
                    case FOUND_ONE_SONG -> {
                        if (!data.targetSong.equals(recognized.foundList.get(0).song())) {
                            data.setTestStatus(Status.WRONG);
                        } else {
                            data.setTestPass(true);
                            data.setTestStatus(
                                    StatusAccuracy.FOUND_EXACT.equals(recognized.statusAccuracy)
                                            ? Status.EXACT
                                            : Status.SIMILAR);
                        }
                    }
                    case NOT_FOUND -> {
                        data.setTestPass(false);
                        data.setTestStatus(Status.NOT_FOUND);
                    }
                }

                onAddData.accept(data);
                updateProgress(i + 1, count);
            }
        }

        return null;
    }
}
