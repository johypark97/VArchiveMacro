package com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model;

import java.util.Objects;

public record Song(int id, String title, String composer, int priority, Pack pack) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Song song = (Song) o;
        return id == song.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
