package com.github.johypark97.varchivemacro.lib.hook;

import com.github.kwhat.jnativehook.NativeLibraryLocator;
import com.github.kwhat.jnativehook.NativeSystem;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.lang.module.ModuleFinder;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JrtNativeLibraryLocator implements NativeLibraryLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JrtNativeLibraryLocator.class);

    private static final String FILE_HASH_ALGORITHM = "SHA-256";

    public static final String JRT_ROOT = "jrt:/";
    public static final String MODULES_PATH = "/modules";

    public static String getHookModuleName() {
        return NativeLibraryLocator.class.getModule().getName();
    }

    public static boolean isHookInJrt() {
        return ModuleFinder.ofSystem().find(getHookModuleName()).isPresent();
    }

    public static Path getLibraryPath() {
        String libraryName = System.getProperty("jnativehook.lib.name", "JNativeHook");
        String libraryFileName = System.mapLibraryName(libraryName);
        return Path.of(System.getProperty("java.home"), libraryFileName);
    }

    protected void copyLibrary(Path inputPath, Path outputPath) throws IOException {
        LOGGER.atDebug().log("Copying {} native library: {} -> {}", getHookModuleName(), inputPath,
                outputPath);
        Files.copy(inputPath, outputPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Iterator<File> getLibraries() {
        Path outputPath = getLibraryPath();

        // throws an exception if a directory with the same name exists and cannot copy the native
        // library
        if (Files.isDirectory(outputPath)) {
            RuntimeException exception = new NativeLibraryCopyException(
                    "Native library copy failed: A directory with the same name already exists.");
            LOGGER.atError().setCause(exception).log();
            throw exception;
        }

        String architecture = NativeSystem.getArchitecture().toString().toLowerCase(Locale.ENGLISH);
        String family = NativeSystem.getFamily().toString().toLowerCase(Locale.ENGLISH);

        try (FileSystem fileSystem = FileSystems.getFileSystem(URI.create(JRT_ROOT))) {
            Path modulePath = fileSystem.getPath(MODULES_PATH, getHookModuleName());

            Optional<Path> optionalPath;
            try (Stream<Path> stream = Files.walk(modulePath)) {
                optionalPath = stream.filter(x -> {
                    if (!Files.isRegularFile(x)) {
                        return false;
                    }

                    // if toString() is not used, the file system is different and endsWith()
                    // returns false
                    if (!x.endsWith(outputPath.getFileName().toString())) {
                        return false;
                    }

                    x = x.getParent();

                    return x.endsWith(architecture) && x.getParent().endsWith(family);
                }).findFirst();
            }

            if (optionalPath.isEmpty()) {
                RuntimeException exception =
                        new UnsupportedOperationException("Unsupported platform.");
                LOGGER.atError().setCause(exception).log();
                throw exception;
            }

            Path inputPath = optionalPath.get();

            if (!Files.exists(outputPath)) {
                LOGGER.atDebug().log("File is not exists.");
                copyLibrary(inputPath, outputPath);
            } else if (Files.size(inputPath) != Files.size(outputPath)) {
                LOGGER.atDebug().log("File size is different.");
                copyLibrary(inputPath, outputPath);
            } else {
                MessageDigest hash = MessageDigest.getInstance(FILE_HASH_ALGORITHM);

                byte[] inputHash = hash.digest(Files.readAllBytes(inputPath));
                byte[] outputHash = hash.digest(Files.readAllBytes(outputPath));

                if (Arrays.equals(inputHash, outputHash)) {
                    LOGGER.atDebug().log("It is a same file. Skip copying the native library.");
                } else {
                    LOGGER.atDebug().log("Hash is different.");
                    copyLibrary(inputPath, outputPath);
                }
            }
        } catch (UnsupportedOperationException ignored) {
            // JrtFileSystem::close will throw UnsupportedOperationException.
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return List.of(outputPath.toFile()).iterator();
    }

    public static class NativeLibraryCopyException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = -8149425611857275876L;

        public NativeLibraryCopyException(String message) {
            super(message);
        }
    }
}
