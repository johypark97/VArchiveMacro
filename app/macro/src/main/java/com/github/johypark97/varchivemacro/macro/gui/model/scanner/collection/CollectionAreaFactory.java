package com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection;

import java.awt.Dimension;

public final class CollectionAreaFactory {
    public static CollectionArea create(Dimension resolution) throws Exception {
        if (resolution.equals(CollectionAreaFHD.RESOLUTION)) {
            return new CollectionAreaFHD();
        }

        String resolutionText = resolution.width + "x" + resolution.height;
        throw new Exception("not supported resolution: " + resolutionText);
    }
}
