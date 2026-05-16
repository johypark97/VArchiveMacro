package com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model;

import java.util.Objects;

public record Category(String name, int priority) implements Comparable<Category> {
    @Override
    public int compareTo(Category o) {
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

        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
