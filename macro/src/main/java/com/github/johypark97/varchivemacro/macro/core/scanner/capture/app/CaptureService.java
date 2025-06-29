package com.github.johypark97.varchivemacro.macro.core.scanner.capture.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;

public class CaptureService implements CaptureRepository {
    private final CaptureRepository captureRepository;

    public CaptureService(CaptureRepository captureRepository) {
        this.captureRepository = captureRepository;
    }

    @Override
    public boolean isEmpty() {
        return captureRepository.isEmpty();
    }

    @Override
    public void deleteAll() {
        captureRepository.deleteAll();
    }

    @Override
    public CaptureEntry save(Capture value) {
        return captureRepository.save(value);
    }

    @Override
    public CaptureEntry findById(int entryId) {
        return captureRepository.findById(entryId);
    }
}
