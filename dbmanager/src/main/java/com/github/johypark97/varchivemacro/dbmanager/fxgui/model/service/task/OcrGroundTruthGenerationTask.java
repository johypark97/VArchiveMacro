package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.util.CacheHelper;
import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import com.google.common.base.CharMatcher;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;

public class OcrGroundTruthGenerationTask extends Task<Void> {
    private static final Path ABSOLUTE_PATH = Path.of("").toAbsolutePath();

    private static final String MODEL_NAME = "djmax";
    private static final String GT_OUTPUT_DIRECTORY_NAME = MODEL_NAME + "-ground-truth";
    private static final String NUMBERS_FILENAME = MODEL_NAME + ".numbers";
    private static final String PUNC_FILENAME = MODEL_NAME + ".punc";
    private static final String WORDLIST_FILENAME = MODEL_NAME + ".wordlist";

    private final CharMatcher letterMatcher;
    private final CharMatcher numberMatcher;
    private final Function<Set<String>, String> joiner;
    private final Function<String, List<String>> splitter;

    public List<Song> songList;
    public TitleTool titleTool;

    public Path inputPath;
    public Path outputPath;

    public OcrGroundTruthGenerationTask() {
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
    protected Void call() throws Exception {
        Objects.requireNonNull(songList);
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

        Path gtOutputDir = outputPath.resolve(GT_OUTPUT_DIRECTORY_NAME);
        Path modelOutputDir = outputPath.resolve(MODEL_NAME);

        CacheHelper.clearAndReadyDirectory(outputPath);
        Files.createDirectories(gtOutputDir);
        Files.createDirectories(modelOutputDir);

        List<Path> imageInputPathList;
        try (Stream<Path> stream = Files.walk(inputPath)) {
            imageInputPathList = stream.filter((x) -> {
                String fileName = x.getFileName().toString();
                return fileName.endsWith(CacheHelper.IMAGE_FORMAT);
            }).sorted(Comparator.reverseOrder()).toList();
        }

        updateProgress(0, 1);

        // create ground truth files
        int count = imageInputPathList.size();
        for (int i = 0; i < count; ++i) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            Path imageInputPath = imageInputPathList.get(i);

            String baseOutputFileName;
            Path gtInputPath;
            {
                Path fileNamePath = imageInputPath.getFileName();
                if (fileNamePath == null) {
                    throw new RuntimeException(
                            "fileNamePath of imageInputPath is null. unexpected error.");
                }

                String inputFileName = fileNamePath.toString();
                inputFileName = inputFileName.substring(0,
                        inputFileName.indexOf(CacheHelper.IMAGE_FORMAT) - 1);

                baseOutputFileName = inputFileName;

                gtInputPath = imageInputPath.resolveSibling(
                        baseOutputFileName + '.' + CacheHelper.GT_FORMAT);
                if (!Files.exists(gtInputPath)) {
                    throw new FileNotFoundException(gtInputPath.toString());
                }
            }

            BufferedImage image = ImageIO.read(imageInputPath.toFile());
            byte[] imageBytes = ImageConverter.imageToPngBytes(image);

            int stretchCondition = 5;
            for (int j = 1; j < 4; ++j) {
                for (int k = 0; k <= stretchCondition * 2; ++k) {
                    float sx = 1 - (k <= stretchCondition ? 0.1f * k : 0);
                    float sy = 1 - (k > stretchCondition ? 0.1f * (k - stretchCondition) : 0);
                    String outputFileName =
                            String.format("%s_%d_%.2f_%.2f.", baseOutputFileName, j, sx, sy);

                    BufferedImage preprocessedImage;
                    try (PixWrapper pix = new PixWrapper(imageBytes)) {
                        PixPreprocessor.preprocessTitle(pix, j, sx, sy);
                        preprocessedImage = ImageConverter.pngBytesToImage(pix.getPngBytes());
                    }
                    preprocessedImage = trimImage(preprocessedImage);

                    Path imageOutputPath =
                            gtOutputDir.resolve(outputFileName + CacheHelper.IMAGE_FORMAT);
                    ImageIO.write(preprocessedImage, CacheHelper.IMAGE_FORMAT,
                            imageOutputPath.toFile());

                    Path gtOutputPath = gtOutputDir.resolve(outputFileName + CacheHelper.GT_FORMAT);
                    Files.copy(gtInputPath, gtOutputPath);
                }
            }

            updateProgress(i + 1, count);
        }

        // create optional files
        {
            Set<String> numberSet = new HashSet<>();
            Set<String> puncSet = new HashSet<>();
            Set<String> wordSet = new HashSet<>();

            for (Song song : songList) {
                String title = titleTool.getClippedTitleOrDefault(song);
                title = TitleTool.normalizeTitle_training(title);

                String numberString = numberMatcher.negate().replaceFrom(title, ' ');
                numberSet.addAll(splitter.apply(numberString));

                String puncString = letterMatcher.replaceFrom(title, ' ');
                puncString = numberMatcher.replaceFrom(puncString, ' ');
                puncSet.addAll(splitter.apply(puncString));

                String wordString = letterMatcher.negate().replaceFrom(title, ' ');
                wordSet.addAll(splitter.apply(wordString));
            }

            Path numbersPath = modelOutputDir.resolve(NUMBERS_FILENAME);
            Path puncPath = modelOutputDir.resolve(PUNC_FILENAME);
            Path wordlistPath = modelOutputDir.resolve(WORDLIST_FILENAME);

            Files.writeString(numbersPath, joiner.apply(numberSet));
            Files.writeString(puncPath, joiner.apply(puncSet));
            Files.writeString(wordlistPath, joiner.apply(wordSet));
        }

        return null;
    }
}
