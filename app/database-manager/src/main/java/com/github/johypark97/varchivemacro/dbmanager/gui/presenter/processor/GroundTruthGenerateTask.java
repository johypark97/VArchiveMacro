package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.GroundTruthGeneratorConfig;
import com.github.johypark97.varchivemacro.lib.common.ImageConverter;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;
import com.google.common.base.CharMatcher;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import javax.imageio.ImageIO;

public class GroundTruthGenerateTask implements Callable<Void> {
    private static final String ENG_DIRECTORY_NAME = "eng";
    private static final String EXCEEDED_DIRECTORY_NAME = "exceeded";
    private static final String KOR_DIRECTORY_NAME = "kor";
    private static final String MIXED_DIRECTORY_NAME = "mixed";

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

    private BufferedImage readTitleImage(Path imagePath) throws IOException, PixError {
        try (PixWrapper pix = new PixWrapper(imagePath)) {
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

    private void writeTitleImage(Path outputDir, LocalSong song, int number, BufferedImage image)
            throws IOException {
        Path path = CacheHelper.createImagePath(outputDir, song, number);
        ImageIO.write(image, CacheHelper.IMAGE_FORMAT, path.toFile());
    }

    private void createGt(Path outputDir, LocalSong song, int number, String title)
            throws IOException {
        Path path = CacheHelper.createGtPath(outputDir, song, number);
        Files.writeString(path, title);
    }

    private void createBox(Path outputDir, LocalSong song, int number, String title,
            BufferedImage image) throws IOException {
        StringBuilder builder = new StringBuilder();
        title.codePoints().forEach((x) -> {
            // write the file using LF only
            String line = String.format("%c 0 0 %d %d 0\n", x, image.getWidth(), image.getHeight());
            builder.append(line);
        });

        Path path = CacheHelper.createBoxPath(outputDir, song, number);
        Files.writeString(path, builder.toString());
    }

    @Override
    public Void call() throws Exception {
        if (!Files.exists(config.inputDir)) {
            throw new RuntimeException("It seems caches are not ready.");
        }

        Path inputDir = config.inputDir;

        Path baseOutputDir = config.outputDir;
        Path engOutputDir = baseOutputDir.resolve(ENG_DIRECTORY_NAME);
        Path korOutputDir = baseOutputDir.resolve(KOR_DIRECTORY_NAME);
        Path mixedOutputDir = baseOutputDir.resolve(MIXED_DIRECTORY_NAME);
        Path exceededOutputDir = baseOutputDir.resolve(EXCEEDED_DIRECTORY_NAME);

        Path numbersPath = baseOutputDir.resolve(NUMBERS_FILENAME);
        Path puncPath = baseOutputDir.resolve(PUNC_FILENAME);
        Path wordlistPath = baseOutputDir.resolve(WORDLIST_FILENAME);

        CacheHelper.clearAndReadyDirectory(baseOutputDir);
        Files.createDirectories(engOutputDir);
        Files.createDirectories(korOutputDir);
        Files.createDirectories(mixedOutputDir);
        Files.createDirectories(exceededOutputDir);

        Set<String> numberSet = new HashSet<>();
        Set<String> puncSet = new HashSet<>();
        Set<String> wordSet = new HashSet<>();

        Thread thread = Thread.currentThread();
        for (LocalSong song : songModel.getSongList()) {
            String title = songModel.getShortTitle(song);
            title = songModel.normalizeTitle(title);

            String numberString = numberMatcher.negate().replaceFrom(title, ' ');
            numberSet.addAll(splitter.apply(numberString));

            String wordString = letterMatcher.negate().replaceFrom(title, ' ');
            wordSet.addAll(splitter.apply(wordString));

            String puncString = letterMatcher.replaceFrom(title, ' ');
            puncString = numberMatcher.replaceFrom(puncString, ' ');
            puncSet.addAll(splitter.apply(puncString));

            boolean containEng = false;
            boolean containKor = false;
            for (int c : title.codePoints().toArray()) {
                if (c >= 0x41 && c <= 0x5A || c >= 0x61 && c <= 0x7A) {
                    containEng = true;
                } else if (c >= 0xAC00 && c <= 0xD7A3) {
                    containKor = true;
                }
            }

            Path outputDir;
            if (songModel.hasShortTitle(song)) {
                outputDir = exceededOutputDir;
            } else if (containEng && !containKor) {
                outputDir = engOutputDir;
            } else if (!containEng && containKor) {
                outputDir = korOutputDir;
            } else {
                outputDir = mixedOutputDir;
            }

            int number = -1;
            while (true) {
                ++number;

                Path imagePath = CacheHelper.createImagePath(inputDir, song, number);
                if (!Files.exists(imagePath)) {
                    break;
                }

                BufferedImage titleImage = readTitleImage(imagePath);
                titleImage = trimImage(titleImage);

                writeTitleImage(outputDir, song, number, titleImage);
                createGt(outputDir, song, number, title);
                createBox(outputDir, song, number, title, titleImage);
            }

            if (thread.isInterrupted()) {
                break;
            }
        }

        Files.writeString(wordlistPath, joiner.apply(wordSet));
        Files.writeString(puncPath, joiner.apply(puncSet));
        Files.writeString(numbersPath, joiner.apply(numberSet));

        return null;
    }
}
