package com.github.johypark97.varchivemacro.macro.infrastructure.github;

import static com.github.johypark97.varchivemacro.macro.infrastructure.github.data.DataVersion.DataFile;
import static com.github.johypark97.varchivemacro.macro.infrastructure.github.data.DataVersion.from;

import com.github.johypark97.varchivemacro.macro.infrastructure.github.data.DataVersion;
import com.github.johypark97.varchivemacro.macro.infrastructure.github.data.GitHubContent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataUpdater {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataUpdater.class);

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String PATH_VERSION = "data/version.json";
    private static final String PATH_VERSION_REQUEST = "macro/" + PATH_VERSION;
    private static final String SUFFIX_UPDATE = ".update";

    protected DataVersion localVersion;
    protected DataVersion remoteVersion;

    public long getCurrentVersion() {
        return localVersion == null ? -1 : localVersion.version();
    }

    public long getLatestVersion() {
        return remoteVersion == null ? -1 : remoteVersion.version();
    }

    public void loadLocalDataVersion() throws IOException {
        localVersion = from(Path.of(PATH_VERSION));
    }

    public void fetchRemoteDataVersion(GitHubApi api) throws IOException, InterruptedException {
        String response = GitHubRequest.getContent(api, PATH_VERSION_REQUEST);
        GitHubContent remoteContent = GitHubContent.from(response);
        byte[] data = Base64.getMimeDecoder().decode(remoteContent.content());
        remoteVersion = from(new String(data));
    }

    public boolean isUpdated() {
        checkLocalVersion();
        checkRemoteVersion();

        return remoteVersion.version() > localVersion.version();
    }

    public void downloadRemoteData(GitHubApi api, RemoteDownloadingHook hook)
            throws IOException, InterruptedException, NoSuchAlgorithmException {
        checkRemoteVersion();

        final int FILE_LIST_COUNT = remoteVersion.fileList().size();
        final int STEP_MAX = FILE_LIST_COUNT + 1;

        for (int i = 0; i < FILE_LIST_COUNT; i++) {
            DataFile dataFile = remoteVersion.fileList().get(i);

            Path path = Path.of(dataFile.path());
            LOGGER.atDebug().log("checking data file: {}", path);
            hook.accept(STEP_MAX, i, path);

            if (Files.exists(path) && compareHash(path, dataFile.hash())) {
                continue;
            }

            Path destinationPath = Path.of(dataFile.path() + SUFFIX_UPDATE);
            downloadContent(api, dataFile.requestPath(), destinationPath);

            LOGGER.atDebug().log("overwriting: {} -> {}", destinationPath, path);
            Files.move(destinationPath, path, StandardCopyOption.REPLACE_EXISTING);
        }

        Path versionPath = Path.of(PATH_VERSION);
        hook.accept(STEP_MAX, STEP_MAX - 1, versionPath);

        Path versionUpdatePath = Path.of(PATH_VERSION + SUFFIX_UPDATE);
        LOGGER.atDebug().log("overwriting: {} -> {}", versionUpdatePath, versionPath);

        remoteVersion.write(versionUpdatePath);
        Files.move(versionUpdatePath, versionPath, StandardCopyOption.ATOMIC_MOVE,
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

    protected void downloadContent(GitHubApi api, String requestPath, Path destinationPath)
            throws IOException, InterruptedException {
        String response = GitHubRequest.getContent(api, requestPath);

        GitHubContent content = GitHubContent.from(response);
        URL downloadUrl = URI.create(content.downloadUrl()).toURL();

        LOGGER.atDebug().log("downloading: {} -> {}", downloadUrl, destinationPath);
        try (InputStream inputStream = downloadUrl.openStream()) {
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    protected boolean compareHash(Path path, String versionHash)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);

        try (InputStream stream = new BufferedInputStream(Files.newInputStream(path));
                DigestInputStream x = new DigestInputStream(stream, digest)) {
            while (x.read() != -1)
                ;
        }

        String fileHash = HexFormat.of().formatHex(digest.digest());

        return versionHash.equals(fileHash);
    }

    @FunctionalInterface
    public interface RemoteDownloadingHook {
        void accept(int maxStep, int currentStep, Path workingFilePath);
    }
}
