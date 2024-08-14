package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.util.CacheHelper;
import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.area.TrainingArea;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;

public class OcrCacheClassificationTask extends Task<Void> {
    private static final Path ABSOLUTE_PATH = Path.of("").toAbsolutePath();
    private static final String CACHE_OVERLAY_FILENAME = "cache_overlay";
    private static final String CACHE_OVERLAY_FORMAT = "png";
    private static final String ENG_DIRECTORY_NAME = "eng";
    private static final String EXCEEDED_DIRECTORY_NAME = "exceeded";
    private static final String KOR_DIRECTORY_NAME = "kor";
    private static final String MIXED_DIRECTORY_NAME = "mixed";

    public List<LocalDlcSong> dlcSongList;
    public TitleTool titleTool;

    public Path inputPath;
    public Path outputPath;

    @Override
    protected Void call() throws Exception {
        Objects.requireNonNull(dlcSongList);
        Objects.requireNonNull(titleTool);

        Objects.requireNonNull(inputPath);
        Objects.requireNonNull(outputPath);

        updateProgress(-1, 0);

        if (!inputPath.isAbsolute()) {
            inputPath = inputPath.toAbsolutePath();
        }

        if (!new PathHelper(inputPath).isSubPathOf(ABSOLUTE_PATH)) {
            throw new RuntimeException("inputPath is not a subdirectory of the program root.");
        } else if (!Files.exists(inputPath)) {
            throw new RuntimeException("inputPath is not exists.");
        } else if (!Files.isDirectory(inputPath)) {
            throw new RuntimeException("inputPath is not a directory.");
        }

        if (!outputPath.isAbsolute()) {
            outputPath = outputPath.toAbsolutePath();
        }

        if (!new PathHelper(outputPath).isSubPathOf(ABSOLUTE_PATH)) {
            throw new RuntimeException("outputPath is not a subdirectory of the program root.");
        }

        Path engOutputDir = outputPath.resolve(ENG_DIRECTORY_NAME);
        Path korOutputDir = outputPath.resolve(KOR_DIRECTORY_NAME);
        Path mixedOutputDir = outputPath.resolve(MIXED_DIRECTORY_NAME);
        Path exceededOutputDir = outputPath.resolve(EXCEEDED_DIRECTORY_NAME);

        CacheHelper.clearAndReadyDirectory(outputPath);
        Files.createDirectories(engOutputDir);
        Files.createDirectories(korOutputDir);
        Files.createDirectories(mixedOutputDir);
        Files.createDirectories(exceededOutputDir);

        updateProgress(0, 1);

        BufferedImage overlayImage = null;
        TrainingArea area = null;

        int count = dlcSongList.size();
        for (int i = 0; i < count; ++i) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            LocalDlcSong song = dlcSongList.get(i);

            String title = titleTool.getClippedTitle(song);
            title = TitleTool.normalizeTitle_training(title);

            boolean containEng = false;
            boolean containKor = false;
            for (int c : title.codePoints().toArray()) {
                if (c >= 0x41 && c <= 0x5A || c >= 0x61 && c <= 0x7A) {
                    containEng = true;
                } else if (c >= 0xAC00 && c <= 0xD7A3) {
                    containKor = true;
                }
            }

            Path baseDir;
            if (titleTool.hasClippedTitle(song)) {
                baseDir = exceededOutputDir;
            } else if (containEng && !containKor) {
                baseDir = engOutputDir;
            } else if (!containEng && containKor) {
                baseDir = korOutputDir;
            } else {
                baseDir = mixedOutputDir;
            }

            Path imageInputPath = CacheHelper.createImagePath(inputPath, song);
            if (Files.exists(imageInputPath)) {
                BufferedImage image = ImageIO.read(imageInputPath.toFile());

                // create a title image
                if (area == null) {
                    Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
                    area = new TrainingArea(CollectionAreaFactory.create(imageSize));
                }

                BufferedImage titleImage = area.getTrainingTitle(image);
                Path imageOutputPath = baseDir.resolve(imageInputPath.getFileName());
                ImageIO.write(titleImage, CacheHelper.IMAGE_FORMAT, imageOutputPath.toFile());

                // create a gt file
                Path gtOutputPath = CacheHelper.createGtPath(baseDir, song);
                Files.writeString(gtOutputPath, title);

                // update the overlay image
                if (overlayImage == null) {
                    Dimension titleSize = area.getTrainingTitle().getSize();
                    overlayImage = new BufferedImage(titleSize.width, titleSize.height,
                            BufferedImage.TYPE_INT_RGB);
                }

                try (PixWrapper pix = new PixWrapper(ImageConverter.imageToPngBytes(titleImage))) {
                    PixPreprocessor.thresholdWhite(pix);
                    BufferedImage preprocessed = ImageConverter.pngBytesToImage(pix.getPngBytes());

                    for (int x = 0; x < preprocessed.getWidth(); ++x) {
                        for (int y = 0; y < preprocessed.getHeight(); ++y) {
                            Color c = new Color(preprocessed.getRGB(x, y));

                            if (Color.WHITE.equals(c)) {
                                overlayImage.setRGB(x, y, Color.WHITE.getRGB());
                            }
                        }
                    }
                }
            }

            updateProgress(i + 1, count);
        }

        if (overlayImage != null) {
            Path overlayPath =
                    outputPath.resolve(CACHE_OVERLAY_FILENAME + '.' + CACHE_OVERLAY_FORMAT);
            ImageIO.write(overlayImage, CACHE_OVERLAY_FORMAT, overlayPath.toFile());
        }

        return null;
    }
}
