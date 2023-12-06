package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.GroundTruthGeneratorConfig;
import com.github.johypark97.varchivemacro.lib.common.ImageConverter;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;
import com.google.common.base.CharMatcher;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

public class GroundTruthGenerateTask implements Callable<Void> {
    private static final String GT_OUTPUT_DIRECTORY_NAME = "gt";

    private static final String NUMBERS_FILENAME = "foo.numbers";
    private static final String PUNC_FILENAME = "foo.punc";
    private static final String WORDLIST_FILENAME = "foo.wordlist";

    private final CharMatcher letterMatcher;
    private final CharMatcher numberMatcher;
    private final Function<Set<String>, String> joiner;
    private final Function<String, List<String>> splitter;

    public GroundTruthGeneratorConfig config;
    public SongModel songModel;

    public GroundTruthGenerateTask() {
        letterMatcher = CharMatcher.forPredicate((x) -> {
            if (x >= 0x41 && x <= 0x5A || x >= 0x61 && x <= 0x7A) { // ascii eng
                return true;
            }
            return x >= 0xAC00 && x <= 0xD7A3; // kor
        }).precomputed();
        numberMatcher = CharMatcher.inRange('0', '9').precomputed();

        joiner = (x) -> String.join("\n", x);
        splitter = (x) -> List.of(x.split("\\s+"));
    }

    public void setConfig(GroundTruthGeneratorConfig config) {
        this.config = config;
    }

    public void setSongModel(SongModel songModel) {
        this.songModel = songModel;
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

    @Override
    public Void call() throws Exception {
        Path inputDir = config.preparedDir;
        Path outputDir = config.groundTruthDir;

        if (!Files.exists(inputDir)) {
            throw new RuntimeException("It seems gt files are not prepared.");
        }

        Path gtOutputDir = outputDir.resolve(GT_OUTPUT_DIRECTORY_NAME);

        Path numbersPath = outputDir.resolve(NUMBERS_FILENAME);
        Path puncPath = outputDir.resolve(PUNC_FILENAME);
        Path wordlistPath = outputDir.resolve(WORDLIST_FILENAME);

        CacheHelper.clearAndReadyDirectory(outputDir);
        Files.createDirectories(gtOutputDir);

        // create ground truth files
        List<Path> imagePathList;
        try (Stream<Path> stream = Files.walk(inputDir)) {
            imagePathList = stream.filter((x) -> {
                String filename = x.getFileName().toString();
                return filename.endsWith(CacheHelper.IMAGE_FORMAT);
            }).sorted(Comparator.reverseOrder()).toList();
        }

        Thread thread = Thread.currentThread();
        for (Path imagePath : imagePathList) {
            Path filenamePath = imagePath.getFileName();
            if (filenamePath == null) {
                throw new RuntimeException("filenamePath is null. unexpected error.");
            }

            String filename = filenamePath.toString();
            filename = filename.substring(0, filename.indexOf(CacheHelper.IMAGE_FORMAT) - 1);

            Path gtPath = imagePath.resolveSibling(filename + '.' + CacheHelper.GT_FORMAT);
            if (!Files.exists(gtPath)) {
                throw new FileNotFoundException(gtPath.toString());
            }

            BufferedImage image = ImageIO.read(imagePath.toFile());
            byte[] imageBytes = ImageConverter.imageToPngBytes(image);

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 5; ++j) {
                    for (int k = 0; k < 5; ++k) {
                        float sx = 1 + 0.05f * j;
                        float sy = 1 + 0.05f * k;
                        String outFilename = String.format("%s_%d_%.2f_%.2f.", filename, i, sx, sy);

                        BufferedImage preprocessedImage;
                        try (PixWrapper pix = new PixWrapper(imageBytes)) {
                            PixPreprocessor.preprocessTitle(pix, i, sx, sy);
                            preprocessedImage = ImageConverter.pngBytesToImage(pix.getPngBytes());
                        }
                        preprocessedImage = trimImage(preprocessedImage);

                        Path imageOutputPath =
                                gtOutputDir.resolve(outFilename + CacheHelper.IMAGE_FORMAT);
                        ImageIO.write(preprocessedImage, CacheHelper.IMAGE_FORMAT,
                                imageOutputPath.toFile());

                        Path gtOutputPath =
                                gtOutputDir.resolve(outFilename + CacheHelper.GT_FORMAT);
                        Files.copy(gtPath, gtOutputPath);
                    }
                }
            }

            if (thread.isInterrupted()) {
                break;
            }
        }

        // create optional files
        Set<String> numberSet = new HashSet<>();
        Set<String> puncSet = new HashSet<>();
        Set<String> wordSet = new HashSet<>();

        for (LocalDlcSong song : songModel.getSongList()) {
            String title = songModel.getTitleTool().getShortTitle(song);
            title = TitleTool.normalizeTitle_training(title);

            String numberString = numberMatcher.negate().replaceFrom(title, ' ');
            numberSet.addAll(splitter.apply(numberString));

            String wordString = letterMatcher.negate().replaceFrom(title, ' ');
            wordSet.addAll(splitter.apply(wordString));

            String puncString = letterMatcher.replaceFrom(title, ' ');
            puncString = numberMatcher.replaceFrom(puncString, ' ');
            puncSet.addAll(splitter.apply(puncString));
        }

        Files.writeString(wordlistPath, joiner.apply(wordSet));
        Files.writeString(puncPath, joiner.apply(puncSet));
        Files.writeString(numbersPath, joiner.apply(numberSet));

        return null;
    }
}
