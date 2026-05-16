package com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model;

import java.util.Objects;

public record Pack(String name, int priority, Category category) implements Comparable<Pack> {
    @Override
    public int compareTo(Pack o) {
        int x = category.compareTo(o.category);
        if (x != 0) {
            return x;
        }

        return priority - o.priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pack pack = (Pack) o;
        return Objects.equals(name, pack.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
