package com.github.johypark97.varchivemacro.macro.common.programdata.infra;

import com.github.johypark97.varchivemacro.macro.common.github.app.GitHubApiService;
import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubContent;
import com.github.johypark97.varchivemacro.macro.common.programdata.app.UpdateProgressHook;
import com.github.johypark97.varchivemacro.macro.common.programdata.domain.DataVersion;
import com.github.johypark97.varchivemacro.macro.common.programdata.infra.model.DataVersionJson;
import com.github.johypark97.varchivemacro.macro.common.programdata.infra.utility.Sha256FileHash;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataUpdater {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataUpdater.class);

    private static final String SUFFIX_UPDATE = ".update";

    private final GitHubApiService gitHubApiService;

    private final Path dataDirectoryPath;
    private final String dataVersionFilename;

    public DataUpdater(GitHubApiService gitHubApiService, Path dataDirectoryPath,
            String dataVersionFilename) {
        this.gitHubApiService = gitHubApiService;

        this.dataDirectoryPath = dataDirectoryPath;
        this.dataVersionFilename = dataVersionFilename;
    }

    public void update(DataVersion remoteDataVersion, UpdateProgressHook hook)
            throws IOException, InterruptedException {
        final int FILE_LIST_COUNT = remoteDataVersion.fileList().size();
        final int STEP_MAX = FILE_LIST_COUNT + 1;

        for (int i = 0; i < FILE_LIST_COUNT; i++) {
            DataVersion.DataFile dataFile = remoteDataVersion.fileList().get(i);

            Path localDataFilePath = Path.of(dataFile.path());
            LOGGER.atDebug().log("checking data file: {}", localDataFilePath);
            hook.accept(i, STEP_MAX, Optional.of(localDataFilePath));

            if (Files.exists(localDataFilePath)) {
                String localDataFileHash = Sha256FileHash.calculate(localDataFilePath);
                if (localDataFileHash.equals(dataFile.hash())) {
                    continue;
                }
            }

            Path downloadPath = Path.of(dataFile.path() + SUFFIX_UPDATE);
            downloadContent(dataFile.requestPath(), downloadPath);

            LOGGER.atDebug().log("overwriting: {} -> {}", downloadPath, localDataFilePath);
            Files.move(downloadPath, localDataFilePath, StandardCopyOption.REPLACE_EXISTING);
        }

        Path localVersionPath = dataDirectoryPath.resolve(dataVersionFilename);
        hook.accept(FILE_LIST_COUNT, STEP_MAX, Optional.of(localVersionPath));

        Path downloadVersionPath = dataDirectoryPath.resolve(dataVersionFilename + SUFFIX_UPDATE);
        LOGGER.atDebug().log("overwriting: {} -> {}", downloadVersionPath, localVersionPath);

        DataVersionJson.from(remoteDataVersion).write(downloadVersionPath);
        Files.move(downloadVersionPath, localVersionPath, StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING);

        hook.accept(STEP_MAX, STEP_MAX, Optional.empty());
    }

    protected void downloadContent(String requestPath, Path destinationPath)
            throws IOException, InterruptedException {
        GitHubContent content = gitHubApiService.fetchContent(requestPath);

        URL downloadUrl = URI.create(content.downloadUrl()).toURL();

        LOGGER.atDebug().log("downloading: {} -> {}", downloadUrl, destinationPath);
        try (InputStream inputStream = downloadUrl.openStream()) {
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
