package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.DefaultOcrTesterModel.DefaultOcrTesterData;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.OcrTesterModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.OcrTesterConfig;
import com.github.johypark97.varchivemacro.lib.common.StringUtils.StringDiff;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class OcrTestTask implements Callable<Void> {
    public OcrTesterConfig config;
    public OcrTesterModel ocrTesterModel;
    public SongModel songModel;

    public Consumer<Integer> whenAdded;
    public Runnable whenCleared;
    public Runnable whenDone;

    public void setConfig(OcrTesterConfig config) {
        this.config = config;
    }

    public void setModels(SongModel songModel, OcrTesterModel ocrTesterModel) {
        this.ocrTesterModel = ocrTesterModel;
        this.songModel = songModel;
    }

    public void setEvents(Runnable whenCleared, Consumer<Integer> whenAdded, Runnable whenDone) {
        this.whenAdded = whenAdded;
        this.whenCleared = whenCleared;
        this.whenDone = whenDone;
    }

    @Override
    public Void call() throws Exception {
        Objects.requireNonNull(config);
        Objects.requireNonNull(ocrTesterModel);
        Objects.requireNonNull(songModel);

        Objects.requireNonNull(whenAdded);
        Objects.requireNonNull(whenCleared);
        Objects.requireNonNull(whenDone);

        if (config.trainedDataDirectory.equals(Path.of(""))) {
            throw new IOException("directory not designated");
        } else if (!Files.isDirectory(config.trainedDataDirectory)) {
            throw new IOException("invalid directory");
        } else if (config.trainedDataLanguage.isBlank()) {
            throw new IOException("invalid language");
        }

        ocrTesterModel.clear();
        whenCleared.run();
        try (OcrWrapper ocr = new DefaultOcrWrapper(config.trainedDataDirectory,
                config.trainedDataLanguage)) {
            for (LocalSong song : songModel.getSongList()) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                Path imagePath = CacheHelper.createImagePath(config.cachePath, song, 0);

                String scannedTitle;
                try (PixWrapper pix = new PixWrapper(imagePath)) {
                    PixPreprocessor.preprocessTitle(pix);
                    scannedTitle = ocr.run(pix.pixInstance).trim();
                }

                String nTitle = songModel.normalizeTitle(songModel.getShortTitle(song));
                StringDiff diff = new StringDiff(scannedTitle, nTitle);

                DefaultOcrTesterData data = new DefaultOcrTesterData(song);
                data.setAccuracy((float) diff.getSimilarity());
                data.setDistance(diff.getDistance());
                data.setNormalizedTitle(nTitle);
                data.setScannedTitle(scannedTitle);

                ocrTesterModel.addData(data);
                whenAdded.accept(ocrTesterModel.getCount() - 1);
            }
        }

        whenDone.run();
        return null;
    }
}
