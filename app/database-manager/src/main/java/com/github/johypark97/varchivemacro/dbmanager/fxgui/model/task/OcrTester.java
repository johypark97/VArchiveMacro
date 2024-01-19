package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.task;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData.Status;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.util.CacheHelper;
import com.github.johypark97.varchivemacro.lib.common.ImageConverter;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.common.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixError;
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
import javax.imageio.ImageIO;

public class OcrTester implements Runnable {
    public List<LocalDlcSong> songList;
    public Path cachePath;
    public Path tessdataPath;
    public String tessdataLanguage;
    public TitleTool titleTool;

    public Consumer<Double> onUpdateProgress;
    public Consumer<OcrTestData> onAddData;
    public Runnable onClearData;

    @Override
    public void run() {
        Objects.requireNonNull(cachePath);
        Objects.requireNonNull(songList);
        Objects.requireNonNull(tessdataLanguage);
        Objects.requireNonNull(tessdataPath);
        Objects.requireNonNull(titleTool);

        Objects.requireNonNull(onAddData);
        Objects.requireNonNull(onClearData);
        Objects.requireNonNull(onUpdateProgress);

        onUpdateProgress.accept(-1.0);

        try {
            if (!Files.isDirectory(cachePath)) {
                throw new IOException("Invalid cache directory");
            } else if (!Files.isDirectory(tessdataPath)) {
                throw new IOException("Invalid tessdata directory");
            } else if (tessdataLanguage.isBlank()) {
                throw new IOException("Invalid tessdata language");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        onClearData.run();
        onUpdateProgress.accept(0.0);

        TitleSongRecognizer<LocalDlcSong> recognizer = new TitleSongRecognizer<>(titleTool);
        recognizer.setSongList(songList);

        try (OcrWrapper ocr = new DefaultOcrWrapper(tessdataPath, tessdataLanguage)) {
            int count = songList.size();
            for (int i = 0; i < count; ++i) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                LocalDlcSong song = songList.get(i);

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
                onUpdateProgress.accept((i + 1.0) / count);
            }
        } catch (IOException | InterruptedException | OcrInitializationError | PixError e) {
            throw new RuntimeException(e);
        }
    }
}
