package com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.infra.service;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public final class WindowsGraphicsCaptureService implements AutoCloseable {
    private static final List<Path> HELPER_DIRECTORIES =
            List.of(Path.of("capture-helper"), Path.of("macro", "capture-helper"));
    private static final String HELPER_EXE = "VArchive.WgcCapture.exe";
    private static final String HELPER_PROJECT = "VArchive.WgcCapture.csproj";
    private static final String SIZE_COMMAND = "size";

    private final Process process;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final Dimension screenSize;

    public WindowsGraphicsCaptureService(boolean toneMapping) throws IOException {
        process = new ProcessBuilder(createCommand(toneMapping)).start();
        reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));

        Thread errorDrainer = new Thread(() -> {
            try {
                process.getErrorStream().transferTo(OutputStream.nullOutputStream());
            } catch (IOException ignored) {
            }
        }, "wgc-helper-error-drainer");
        errorDrainer.setDaemon(true);
        errorDrainer.start();

        String line = reader.readLine();
        if (!"READY".equals(line)) {
            stopProcess();
            throw new IOException("Failed to start Windows Graphics Capture helper: " + line);
        }

        screenSize = requestSize();
    }

    private static List<String> createCommand(boolean toneMapping) {
        for (Path helperDirectory : HELPER_DIRECTORIES) {
            Path helperExe = helperDirectory.resolve(HELPER_EXE);
            if (Files.isRegularFile(helperExe)) {
                return toneMapping ? List.of(helperExe.toString(), "--tone-map", "--server")
                        : List.of(helperExe.toString(), "--server");
            }
        }

        Path helperProject = HELPER_DIRECTORIES.stream().map(x -> x.resolve(HELPER_PROJECT))
                .filter(Files::isRegularFile).findFirst()
                .orElse(HELPER_DIRECTORIES.get(0).resolve(HELPER_PROJECT));
        List<String> command = new ArrayList<>();
        command.add("dotnet");
        command.add("run");
        command.add("--project");
        command.add(helperProject.toString());
        command.add("--");
        if (toneMapping) {
            command.add("--tone-map");
        }
        command.add("--server");

        return command;
    }

    public Dimension captureSize() {
        return new Dimension(screenSize);
    }

    public synchronized BufferedImage capture() throws IOException {
        Path tempFile = Files.createTempFile("vamacro-wgc-", ".bmp");
        try {
            writer.write(tempFile.toAbsolutePath().toString());
            writer.newLine();
            writer.flush();

            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Windows Graphics Capture helper has stopped.");
            }
            if (!"OK".equals(line)) {
                throw new IOException("Windows Graphics Capture helper error: " + line);
            }

            BufferedImage image = ImageIO.read(tempFile.toFile());
            if (image == null) {
                throw new IOException("Windows Graphics Capture helper wrote an invalid image.");
            }

            return image;
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private synchronized Dimension requestSize() throws IOException {
        writer.write(SIZE_COMMAND);
        writer.newLine();
        writer.flush();

        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Windows Graphics Capture helper has stopped.");
        }

        String[] tokens = line.split(" ");
        if (tokens.length != 3 || !"SIZE".equals(tokens[0])) {
            throw new IOException("Unexpected Windows Graphics Capture helper size: " + line);
        }

        return new Dimension(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
    }

    @Override
    public synchronized void close() throws IOException {
        stopProcess();
    }

    private void stopProcess() throws IOException {
        try {
            if (process.isAlive()) {
                writer.write("exit");
                writer.newLine();
                writer.flush();
            }
        } finally {
            process.destroy();
        }
    }
}
