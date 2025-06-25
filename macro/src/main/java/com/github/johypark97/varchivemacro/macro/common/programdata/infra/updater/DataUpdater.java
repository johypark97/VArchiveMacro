package com.github.johypark97.varchivemacro.macro.common.programdata.infra.updater;

import com.github.johypark97.varchivemacro.macro.common.github.infra.api.GitHubApi;
import com.github.johypark97.varchivemacro.macro.common.github.infra.model.GitHubContent;
import com.github.johypark97.varchivemacro.macro.common.programdata.infra.model.DataVersion;
import com.github.johypark97.varchivemacro.macro.common.programdata.infra.utility.Sha256FileHash;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataUpdater {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataUpdater.class);

    private static final String SUFFIX_UPDATE = ".update";
    private static final String VERSION_FILENAME = "version.json";
    private static final String VERSION_REQUEST_PATH = "macro/" + VERSION_FILENAME;

    private final Path dataDirectoryPath;

    protected DataVersion localVersion;
    protected DataVersion remoteVersion;

    public DataUpdater(Path dataDirectoryPath) {
        this.dataDirectoryPath = dataDirectoryPath;
    }

    public long getCurrentVersion() {
        return localVersion == null ? -1 : localVersion.version();
    }

    public long getLatestVersion() {
        return remoteVersion == null ? -1 : remoteVersion.version();
    }

    public void loadLocalDataVersion() throws IOException {
        localVersion = DataVersion.from(dataDirectoryPath.resolve(VERSION_FILENAME));
    }

    public void fetchRemoteDataVersion(GitHubApi api, String owner, String repository)
            throws IOException, InterruptedException {
        String response = getContent(api, owner, repository, VERSION_REQUEST_PATH);
        GitHubContent remoteContent = GitHubContent.from(response);
        byte[] data = Base64.getMimeDecoder().decode(remoteContent.content());
        remoteVersion = DataVersion.from(new String(data));
    }

    public boolean isUpdated() {
        checkLocalVersion();
        checkRemoteVersion();

        return remoteVersion.version() > localVersion.version();
    }

    public void updateData(GitHubApi api, String owner, String repository,
            RemoteDownloadingHook hook)
            throws IOException, InterruptedException, NoSuchAlgorithmException {
        checkRemoteVersion();

        final int FILE_LIST_COUNT = remoteVersion.fileList().size();
        final int STEP_MAX = FILE_LIST_COUNT + 1;

        for (int i = 0; i < FILE_LIST_COUNT; i++) {
            DataVersion.DataFile dataFile = remoteVersion.fileList().get(i);

            Path localDataFilePath = Path.of(dataFile.path());
            LOGGER.atDebug().log("checking data file: {}", localDataFilePath);
            hook.accept(STEP_MAX, i, localDataFilePath);

            if (Files.exists(localDataFilePath)) {
                String localDataFileHash = Sha256FileHash.calculate(localDataFilePath);
                if (localDataFileHash.equals(dataFile.hash())) {
                    continue;
                }
            }

            Path downloadPath = Path.of(dataFile.path() + SUFFIX_UPDATE);
            downloadContent(api, owner, repository, dataFile.requestPath(), downloadPath);

            LOGGER.atDebug().log("overwriting: {} -> {}", downloadPath, localDataFilePath);
            Files.move(downloadPath, localDataFilePath, StandardCopyOption.REPLACE_EXISTING);
        }

        Path localVersionPath = dataDirectoryPath.resolve(VERSION_FILENAME);
        hook.accept(STEP_MAX, STEP_MAX - 1, localVersionPath);

        Path downloadVersionPath = dataDirectoryPath.resolve(VERSION_FILENAME + SUFFIX_UPDATE);
        LOGGER.atDebug().log("overwriting: {} -> {}", downloadVersionPath, localVersionPath);

        remoteVersion.write(downloadVersionPath);
        Files.move(downloadVersionPath, localVersionPath, StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING);
    }

    protected void checkLocalVersion() {
        if (localVersion == null) {
            throw new IllegalStateException("Local DataVersion is not loaded.");
        }
    }

    protected void checkRemoteVersion() {
        if (remoteVersion == null) {
            throw new IllegalStateException("Remote DataVersion is not fetched.");
        }
    }

    protected String getContent(GitHubApi api, String owner, String repository, String path)
            throws IOException, InterruptedException {
        URI uri = GitHubApi.UriBuilder.create_content(owner, repository, path);

        HttpResponse<String> httpResponse = api.send(uri);
        if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(
                    String.format("[%d] %s", httpResponse.statusCode(), httpResponse.body()));
        }

        return httpResponse.statusCode() == HttpURLConnection.HTTP_OK ? httpResponse.body() : "";
    }

    protected void downloadContent(GitHubApi api, String owner, String repository,
            String requestPath, Path destinationPath) throws IOException, InterruptedException {
        String response = getContent(api, owner, repository, requestPath);

        GitHubContent content = GitHubContent.from(response);
        URL downloadUrl = URI.create(content.downloadUrl()).toURL();

        LOGGER.atDebug().log("downloading: {} -> {}", downloadUrl, destinationPath);
        try (InputStream inputStream = downloadUrl.openStream()) {
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @FunctionalInterface
    public interface RemoteDownloadingHook {
        void accept(int maxStep, int currentStep, Path workingFilePath);
    }
}
