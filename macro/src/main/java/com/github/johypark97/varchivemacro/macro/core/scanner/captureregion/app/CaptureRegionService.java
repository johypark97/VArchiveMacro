package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.domain.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.exception.DisplayResolutionException;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.factory.CaptureRegionFactory;
import java.awt.Dimension;

public class CaptureRegionService {
    public CaptureRegion create(Dimension resolution) throws DisplayResolutionException {
        return CaptureRegionFactory.create(resolution);
    }
}
