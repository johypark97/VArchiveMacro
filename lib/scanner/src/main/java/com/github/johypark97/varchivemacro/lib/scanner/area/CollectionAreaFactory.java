package com.github.johypark97.varchivemacro.lib.scanner.area;

import com.google.common.math.IntMath;
import java.awt.Dimension;
import java.awt.Point;

public final class CollectionAreaFactory {
    public static CollectionArea create(Dimension resolution)
            throws NotSupportedResolutionException {
        int gcd = IntMath.gcd(resolution.width, resolution.height);
        Point ratio = new Point(resolution.width / gcd, resolution.height / gcd);

        if (CollectionAreaFHD.RESOLUTION_RATIO.equals(ratio) && checkMinimumResolution(
                CollectionAreaFHD.RESOLUTION_MINIMUM, resolution)) {
            return new CollectionAreaFHD(resolution);
        }

        throw new NotSupportedResolutionException(resolution);
    }

    private static boolean checkMinimumResolution(Dimension area, Dimension screen) {
        return screen.width >= area.width && screen.height >= area.height;
    }
}
