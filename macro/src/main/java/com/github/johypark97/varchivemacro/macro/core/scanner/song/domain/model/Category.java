package com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model;

public record Category(String name, int priority) implements Comparable<Category> {
    @Override
    public int compareTo(Category o) {
        return Integer.compare(priority(), o.priority());
    }
}
