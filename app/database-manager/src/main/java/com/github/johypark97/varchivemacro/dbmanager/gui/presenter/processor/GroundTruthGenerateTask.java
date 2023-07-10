package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.GroundTruthGeneratorConfig;
import com.github.johypark97.varchivemacro.lib.common.ImageConverter;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.common.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;

public class GroundTruthGenerateTask implements Callable<Void> {
    private static final String EXCEEDED_DIRECTORY_NAME = "exceeded";

    public GroundTruthGeneratorConfig config;
    public SongModel songModel;

    public void setConfig(GroundTruthGeneratorConfig config) {
        this.config = config;
    }

    public void setSongModel(SongModel songModel) {
        this.songModel = songModel;
    }

    private BufferedImage readTitleImage(Path inputDir, LocalSong song)
            throws IOException, NotSupportedResolutionException, PixError {
        Path inputPath = CacheHelper.createImagePath(inputDir, song);

        BufferedImage image = ImageIO.read(inputPath.toFile());
        Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
        CollectionArea area = CollectionAreaFactory.create(imageSize);

        byte[] bytes = ImageConverter.imageToPngBytes(area.getTitle(image));
        try (PixWrapper pix = new PixWrapper(bytes)) {
            PixPreprocessor.preprocessTitle(pix);
            return ImageConverter.pngBytesToImage(pix.getPngBytes());
        }
    }

    private BufferedImage trimImage(BufferedImage image) {
        int bottom = 0;
        int left = image.getWidth();
        int right = 0;
        int top = image.getHeight();

        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                Color c = new Color(image.getRGB(x, y));

                if (!Color.WHITE.equals(c)) {
                    bottom = Math.max(bottom, y);
                    left = Math.min(left, x);
                    right = Math.max(right, x);
                    top = Math.min(top, y);
                }
            }
        }

        return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    private void writeTitleImage(Path outputDir, LocalSong song, BufferedImage image)
            throws IOException {
        Path path = CacheHelper.createImagePath(outputDir, song);
        ImageIO.write(image, CacheHelper.IMAGE_FORMAT, path.toFile());
    }

    private void createGt(Path outputDir, LocalSong song) throws IOException {
        Path path = CacheHelper.createGtPath(outputDir, song);
        String title = songModel.getShortTitle(song);
        Files.writeString(path, songModel.normalizeTitle(title));
    }

    private void createBox(Path outputDir, LocalSong song, BufferedImage image) throws IOException {
        StringBuilder builder = new StringBuilder();

        String title = songModel.getShortTitle(song);
        title = songModel.normalizeTitle(title);
        title.codePoints().forEach((x) -> {
            // write the file using LF only
            String line = String.format("%c 0 0 %d %d 0\n", x, image.getWidth(), image.getHeight());
            builder.append(line);
        });

        Path path = CacheHelper.createBoxPath(outputDir, song);
        Files.writeString(path, builder.toString());
    }

    @Override
    public Void call() throws Exception {
        if (!Files.exists(config.inputDir)) {
            throw new RuntimeException("It seems caches are not ready.");
        }

        Path inputDir = config.inputDir;
        Path normalOutputDir = config.outputDir;
        Path exceededOutputDir = normalOutputDir.resolve(EXCEEDED_DIRECTORY_NAME);
        Thread thread = Thread.currentThread();

        CacheHelper.clearAndReadyDirectory(normalOutputDir);
        Files.createDirectories(exceededOutputDir);

        for (LocalSong song : songModel.getSongList()) {
            Path outputDir = songModel.hasShortTitle(song) ? exceededOutputDir : normalOutputDir;

            BufferedImage titleImage = readTitleImage(inputDir, song);
            titleImage = trimImage(titleImage);

            writeTitleImage(outputDir, song, titleImage);
            createGt(outputDir, song);
            createBox(outputDir, song, titleImage);

            if (thread.isInterrupted()) {
                break;
            }
        }

        return null;
    }
}
