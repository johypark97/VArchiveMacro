package com.github.johypark97.varchivemacro.macro.infrastructure.github.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Semver(int major, int minor, int patch) implements Comparable<Semver> {
    private static final String REGEX = "^v?(\\d+)\\.(\\d+)\\.(\\d+)";

    public static Semver from(String version) {
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(version);

        if (!matcher.find()) {
            throw new IllegalArgumentException(String.format("Not a valid semver: %s", version));
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));

        return new Semver(major, minor, patch);
    }

    @Override
    public int compareTo(Semver o) {
        int x;

        x = major - o.major;
        if (x != 0) {
            return x;
        }

        x = minor - o.minor;
        if (x != 0) {
            return x;
        }

        return patch - o.patch;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }
}
