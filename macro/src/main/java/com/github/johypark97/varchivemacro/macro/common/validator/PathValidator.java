package com.github.johypark97.varchivemacro.macro.common.validator;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class PathValidator {
    public static Path validateAndConvert(String path) throws IOException {
        try {
            return Path.of(path);
        } catch (InvalidPathException e) {
            throw new IOException("Invalid path: " + path, e);
        }
    }
}
