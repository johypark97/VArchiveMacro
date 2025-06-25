package com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model;

public record Song(int songId, String title, String composer, int priority, Pack pack) {
    public record Pack(String name, int priority, Category category) {
        public record Category(String name, int priority) {
        }
    }
}
