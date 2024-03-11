package com.github.johypark97.varchivemacro.lib.common;

import java.nio.file.Path;

public class PathHelper {
    private final Path path;

    public PathHelper(Path path) {
        this.path = path;
    }

    public boolean isSubPathOf(Path other) {
        return path.startsWith(other) && !path.equals(other);
    }
}
