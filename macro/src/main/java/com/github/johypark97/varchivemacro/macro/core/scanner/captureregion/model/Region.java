package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model;

public record Region(int x, int y, int width, int height) {
    public Region move(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return this;
        }

        return new Region(x + dx, y + dy, width, height);
    }

    public Region expand(int margin) {
        if (margin == 0) {
            return this;
        }

        return new Region(x - margin, y - margin, width + 2 * margin, height + 2 * margin);
    }
}
