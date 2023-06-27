package com.github.johypark97.varchivemacro.lib.common.area;

import java.awt.Dimension;

public final class CollectionAreaFactory {
    public static CollectionArea create(Dimension resolution)
            throws NotSupportedResolutionException {
        if (resolution.equals(CollectionAreaFHD.RESOLUTION)) {
            return new CollectionAreaFHD();
        }

        throw new NotSupportedResolutionException(resolution);
    }
}
