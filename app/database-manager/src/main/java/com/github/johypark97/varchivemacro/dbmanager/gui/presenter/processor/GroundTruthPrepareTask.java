package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.GroundTruthGeneratorConfig;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class GroundTruthPrepareTask implements Callable<Void> {
    private static final String ENG_DIRECTORY_NAME = "eng";
    private static final String EXCEEDED_DIRECTORY_NAME = "exceeded";
    private static final String KOR_DIRECTORY_NAME = "kor";
    private static final String MIXED_DIRECTORY_NAME = "mixed";

    public GroundTruthGeneratorConfig config;
    public SongModel songModel;

    public void setConfig(GroundTruthGeneratorConfig config) {
        this.config = config;
    }

    public void setSongModel(SongModel songModel) {
        this.songModel = songModel;
    }

    @Override
    public Void call() throws Exception {
        Path inputDir = config.cacheDir;
        Path outputDir = config.preparedDir;

        if (!Files.exists(inputDir)) {
            throw new RuntimeException("It seems caches are not ready.");
        }

        Path engOutputDir = outputDir.resolve(ENG_DIRECTORY_NAME);
        Path korOutputDir = outputDir.resolve(KOR_DIRECTORY_NAME);
        Path mixedOutputDir = outputDir.resolve(MIXED_DIRECTORY_NAME);
        Path exceededOutputDir = outputDir.resolve(EXCEEDED_DIRECTORY_NAME);

        CacheHelper.clearAndReadyDirectory(outputDir);
        Files.createDirectories(engOutputDir);
        Files.createDirectories(korOutputDir);
        Files.createDirectories(mixedOutputDir);
        Files.createDirectories(exceededOutputDir);

        Thread thread = Thread.currentThread();
        for (LocalDlcSong song : songModel.getSongList()) {
            String title = songModel.getTitleTool().getShortTitle(song);
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
            if (songModel.getTitleTool().hasShortTitle(song)) {
                baseDir = exceededOutputDir;
            } else if (containEng && !containKor) {
                baseDir = engOutputDir;
            } else if (!containEng && containKor) {
                baseDir = korOutputDir;
            } else {
                baseDir = mixedOutputDir;
            }

            int number = -1;
            while (true) {
                ++number;

                Path imageInputPath = CacheHelper.createImagePath(inputDir, song, number);
                if (!Files.exists(imageInputPath)) {
                    break;
                }

                Path imageOutputPath = baseDir.resolve(imageInputPath.getFileName());
                Files.copy(imageInputPath, imageOutputPath);

                Path path = CacheHelper.createGtPath(baseDir, song, number);
                Files.writeString(path, title);
            }

            if (thread.isInterrupted()) {
                break;
            }
        }

        return null;
    }
}
