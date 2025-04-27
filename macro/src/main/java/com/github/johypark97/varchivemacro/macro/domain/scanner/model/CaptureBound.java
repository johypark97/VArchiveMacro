package com.github.johypark97.varchivemacro.macro.domain.scanner.model;

public record CaptureBound(int x, int y, int width, int height) {
    public CaptureBound {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height values must be positive.");
        }
    }
}
