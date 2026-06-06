package com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model;

public record Pack(String name, int priority, Category category) implements Comparable<Pack> {
    @Override
    public int compareTo(Pack o) {
        int x = category.compareTo(o.category);
        if (x != 0) {
            return x;
        }

        return Integer.compare(priority(), o.priority());
    }
}
