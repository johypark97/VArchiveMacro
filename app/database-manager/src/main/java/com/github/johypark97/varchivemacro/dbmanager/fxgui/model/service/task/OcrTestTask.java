package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData.Status;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.util.CacheHelper;
import com.github.johypark97.varchivemacro.lib.common.ImageConverter;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.common.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.lib.common.recognizer.TitleSongRecognizer;
import com.github.johypark97.varchivemacro.lib.common.recognizer.TitleSongRecognizer.Recognized;
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

        TitleSongRecognizer<LocalDlcSong> recognizer = new TitleSongRecognizer<>(titleTool);
        recognizer.setSongList(dlcSongList);

        try (OcrWrapper ocr = new DefaultOcrWrapper(tessdataPath, tessdataLanguage)) {
            int count = dlcSongList.size();
            for (int i = 0; i < count; ++i) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                LocalDlcSong song = dlcSongList.get(i);

                Path imagePath = CacheHelper.createImagePath(cachePath, song);
                BufferedImage image = ImageIO.read(imagePath.toFile());
                image = CollectionArea.cropTitleMargin(image);

                String scannedTitle;
                try (PixWrapper pix = new PixWrapper(ImageConverter.imageToPngBytes(image))) {
                    PixPreprocessor.preprocessTitle(pix);
                    scannedTitle = ocr.run(pix.pixInstance);
                }

                OcrTestData data = new OcrTestData(song);

                Recognized<LocalDlcSong> recognized = recognizer.recognize(scannedTitle);
                data.setMatchFoundKey(recognized.foundKey());
                data.setMatchScannedTitle(recognized.normalizedInput());

                switch (recognized.status()) {
                    case DUPLICATED_SONG -> {
                        data.setRecognizedSong(recognized.song());
                        data.setTestPass(true);
                        data.setTestStatus(Status.DUPLICATED);
                    }
                    case FOUND -> {
                        data.setRecognizedSong(recognized.song());
                        data.setTestAccuracy(recognized.similarity());
                        data.setTestDistance(recognized.distance());

                        if (recognized.song().equals(song)) {
                            data.setTestPass(true);
                            data.setTestStatus(
                                    (recognized.distance() == 0) ? Status.EXACT : Status.SIMILAR);
                        } else {
                            data.setTestPass(false);
                            data.setTestStatus(Status.WRONG);
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
