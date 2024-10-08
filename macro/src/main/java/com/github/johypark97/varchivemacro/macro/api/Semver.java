package com.github.johypark97.varchivemacro.macro.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Semver {
    private static final String REGEX = "^v?(\\d+)\\.(\\d+)\\.(\\d+)";

    public final int major;
    public final int minor;
    public final int patch;

    public Semver(String version) {
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(version);

        if (!matcher.find()) {
            throw new IllegalArgumentException(String.format("Not a valid semver: %s", version));
        }

        major = Integer.parseInt(matcher.group(1));
        minor = Integer.parseInt(matcher.group(2));
        patch = Integer.parseInt(matcher.group(3));
    }

    public static int compare(Semver v1, Semver v2) {
        int x;

        if ((x = v1.major - v2.major) != 0) {
            return x;
        }

        if ((x = v1.minor - v2.minor) != 0) {
            return x;
        }

        return v1.patch - v2.patch;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }
}
