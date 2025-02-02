package com.github.johypark97.varchivemacro.lib.common;

import java.nio.file.Path;

public record PathHelper(Path path) {
    public boolean isSubPathOf(Path other) {
        return isSubPathOf(other, false);
    }

    public boolean isSubPathOf(Path other, boolean includeSelf) {
        if (!path.startsWith(other)) {
            return false;
        }

        return includeSelf || !path.equals(other);
    }

    public Path toRelativeOfOrNot(Path other) {
        if (!path.startsWith(other)) {
            return path;
        }

        return path.equals(other) ? Path.of(".") : other.relativize(path);
    }
}
