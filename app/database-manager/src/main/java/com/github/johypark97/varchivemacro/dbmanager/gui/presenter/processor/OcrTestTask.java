package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.DefaultOcrTesterModel.DefaultOcrTesterData;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.OcrTesterModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.OcrTesterConfig;
import com.github.johypark97.varchivemacro.lib.common.ImageConverter;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.ocr.DefaultOcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

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

        if (!Files.exists(config.trainedDataPath)) {
            throw new FileNotFoundException("trained data file not found");
        } else if (!Files.isRegularFile(config.trainedDataPath)) {
            throw new IOException("trained data path is not a file");
        }

        Path dataDir = config.trainedDataPath.toAbsolutePath().getParent();
        Path filenamePath = config.trainedDataPath.getFileName();
        if (filenamePath == null) {
            throw new RuntimeException();
        }
        String filename = filenamePath.toString();
        String language = filename.substring(0, filename.indexOf('.'));

        ocrTesterModel.clear();
        whenCleared.run();
        try (OcrWrapper ocr = new DefaultOcrWrapper(dataDir, language)) {
            for (LocalSong song : songModel.getSongList()) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                Path imagePath = CacheHelper.createImagePath(config.cachePath, song);
                BufferedImage image = ImageIO.read(imagePath.toFile());

                Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
                CollectionArea area = CollectionAreaFactory.create(imageSize);

                String scannedTitle;
                byte[] bytes = ImageConverter.imageToPngBytes(area.getTitle(image));
                try (PixWrapper pix = new PixWrapper(bytes)) {
                    PixPreprocessor.preprocessTitle(pix);
                    scannedTitle = ocr.run(pix.pixInstance).trim();
                }

                DefaultOcrTesterData data = new DefaultOcrTesterData(song);
                data.setNormalizedTitle(songModel.normalizeTitle(songModel.getShortTitle(song)));
                data.setScannedTitle(scannedTitle);

                ocrTesterModel.addData(data);
                whenAdded.accept(ocrTesterModel.getCount() - 1);
            }
        }

        whenDone.run();
        return null;
    }
}
