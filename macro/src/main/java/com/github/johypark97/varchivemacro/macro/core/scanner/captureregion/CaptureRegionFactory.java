package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.exception.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.Resolution;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.TrainingRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.impl.CaptureRegionFHD;
import java.util.HashMap;
import java.util.Map;

public final class CaptureRegionFactory {
    private static final Map<Resolution, CaptureRegion> cache = new HashMap<>();

    public CaptureRegionFactory() {
        CaptureRegion fhd = new CaptureRegionFHD();
        cache.put(fhd.getResolution(), fhd);
    }

    public static CaptureRegion create(int width, int height)
            throws NotSupportedResolutionException {
        Resolution resolution = new Resolution(width, height);

        CaptureRegion captureRegion = cache.get(resolution);
        if (captureRegion == null) {
            throw new NotSupportedResolutionException(resolution);
        }

        return captureRegion;
    }

    public static TrainingRegion createTrainingRegion(int width, int height)
            throws NotSupportedResolutionException {
        return new TrainingRegion(create(width, height));
    }
}
