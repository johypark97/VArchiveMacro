package com.github.johypark97.varchivemacro.macro.domain.scanner.repository;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Capture;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureEntry;

public interface CaptureRepository {
    boolean isEmpty();

    void deleteAll();

    CaptureEntry save(Capture value);

    CaptureEntry findById(int entryId);
}
