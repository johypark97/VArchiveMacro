package com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;

public interface CaptureRepository {
    boolean isEmpty();

    void deleteAll();

    CaptureEntry save(Capture value);

    CaptureEntry findById(int entryId);
}
