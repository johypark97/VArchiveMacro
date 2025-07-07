package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.factory;

import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordButtonConverter;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordPatternConverter;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.domain.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.exception.DisplayResolutionException;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.model.CaptureRegionImpl;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import java.awt.Dimension;

public class CaptureRegionFactory {
    public static CaptureRegion create(Dimension resolution) throws DisplayResolutionException {
        CollectionArea area;

        try {
            area = CollectionAreaFactory.create(resolution);
        } catch (NotSupportedResolutionException e) {
            throw new DisplayResolutionException(e.getMessage(), e);
        }

        CaptureRegionImpl region = new CaptureRegionImpl();

        region.title = area.getTitle();

        for (RecordButton button : RecordButton.values()) {
            Enums.Button libButton = RecordButtonConverter.toLib(button);

            for (RecordPattern pattern : RecordPattern.values()) {
                Enums.Pattern libPattern = RecordPatternConverter.toLib(pattern);

                region.rateArray[button.getWeight()][pattern.getWeight()] =
                        area.getRate(libButton, libPattern);

                region.maxComboArray[button.getWeight()][pattern.getWeight()] =
                        area.getComboMark(libButton, libPattern);
            }
        }

        return region;
    }
}
