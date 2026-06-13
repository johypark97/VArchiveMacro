package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model;

import com.google.common.math.IntMath;
import java.util.Objects;

public class Resolution {
    private final int width;
    private final int height;

    private final int ratioWidth;
    private final int ratioHeight;

    public Resolution(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive integers.");
        }

        this.width = width;
        this.height = height;

        int gcd = IntMath.gcd(width, height);
        this.ratioWidth = width / gcd;
        this.ratioHeight = height / gcd;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRatioWidth() {
        return ratioWidth;
    }

    public int getRatioHeight() {
        return ratioHeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Resolution that)) {
            return false;
        }

        return width == that.width && height == that.height;
    }
}
